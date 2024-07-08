package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.Constants.DEFAULT_MULTICAST_ADDRESS
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.MulticastSocket

/** Specific implementation of [SocketListener] */
internal class JvmSocketListener(
    private val retryCount: Int,
    private val logger: LighthouseLogger? = null,
) : SocketListener {

    private val multicastGroup: InetAddress by lazy {
        InetAddress.getByName(DEFAULT_MULTICAST_ADDRESS)
    }

    private fun setupSocket(): MulticastSocket {
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
            multicastSocket.use {
                val datagramPacketRequest = searchRequest.toDatagramPacket(multicastGroup)

                repeat(retryCount + 1) { multicastSocket.send(datagramPacketRequest) }

                while (currentCoroutineContext().isActive) {
                    val discoveryBuffer = ByteArray(MULTICAST_DATAGRAM_SIZE)
                    val discoveryDatagram =
                        DatagramPacket(discoveryBuffer, discoveryBuffer.size)
                    it.receive(discoveryDatagram)
                    emit(discoveryDatagram)
                }
            }
        }
            .onCompletion { teardownSocket(multicastSocket) }
    }

    private fun teardownSocket(multicastSocket: MulticastSocket) {
        logger?.logStatusMessage(TAG, "Releasing resources")

        if (!multicastSocket.isClosed) {
            multicastSocket.leaveGroup(multicastGroup)
            multicastSocket.close()
        }
    }

    private companion object {
        const val TAG = "RealSocketListener"
        const val MULTICAST_DATAGRAM_SIZE = 2048
        const val MULTICAST_PORT = 1900
    }
}
