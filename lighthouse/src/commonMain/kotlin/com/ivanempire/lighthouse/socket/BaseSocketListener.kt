package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.Constants.DEFAULT_MULTICAST_ADDRESS
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket

/** Specific implementation of [SocketListener] */
internal abstract class BaseSocketListener(
    private val retryCount: Int,
    private val logger: LighthouseLogger? = null,
) : SocketListener {

    private val multicastGroup: InetAddress by lazy {
        InetAddress.getByName(DEFAULT_MULTICAST_ADDRESS)
    }

    protected abstract fun acquireMulticastLock()

    private fun setupSocket(): MulticastSocket {
        acquireMulticastLock()

        val multicastSocket = MulticastSocket(null)
        multicastSocket.reuseAddress = true
        multicastSocket.broadcast = true
        multicastSocket.loopbackMode = true // disable LoopbackMode

        try {
            multicastSocket.joinGroup(multicastGroup)
            multicastSocket.bind(InetSocketAddress(MULTICAST_PORT))
            logger?.logStatusMessage(TAG, "MulticastSocket has been setup")
        } catch (ex: Exception) {
            logger?.logErrorMessage(
                TAG,
                "Could finish setting up the multicast socket and group",
                ex
            )
        }

        return multicastSocket
    }

    override fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket> {
        logger?.logStatusMessage(TAG, "Setting up datagram packet flow")
        val multicastSocket = setupSocket()

        return flow {
                val datagramPacketRequest = searchRequest.toDatagramPacket(multicastGroup)

                repeat(retryCount + 1) {
                    if (!multicastSocket.isClosed) {
                        multicastSocket.send(datagramPacketRequest)
                    }
                }

                while (currentCoroutineContext().isActive && !multicastSocket.isClosed) {
                    val discoveryBuffer = ByteArray(MULTICAST_DATAGRAM_SIZE)
                    val discoveryDatagram =
                        DatagramPacket(discoveryBuffer, discoveryBuffer.size)
                    multicastSocket.receive(discoveryDatagram)
                    emit(discoveryDatagram)
                }
            }
            .catch { cause ->
                logger?.logErrorMessage(TAG, "", cause)
            }
            .onCompletion {
                teardownSocket(multicastSocket)
            }
            .flowOn(Dispatchers.IO)
    }

    protected abstract fun releaseMulticastLock()

    private fun teardownSocket(multicastSocket: MulticastSocket) {
        logger?.logStatusMessage(TAG, "Releasing resources")

        releaseMulticastLock()

        if (!multicastSocket.isClosed) {
            multicastSocket.leaveGroup(multicastGroup)
            multicastSocket.close()
        }
    }

    private companion object {
        const val TAG = "BaseSocketListener"
        const val MULTICAST_DATAGRAM_SIZE = 2048
        const val MULTICAST_PORT = 1900
    }
}
