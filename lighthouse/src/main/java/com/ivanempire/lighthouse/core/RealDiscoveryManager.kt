package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformer
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParser
import com.ivanempire.lighthouse.socket.SocketListener
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.isActive

/**
 * Specific implementation of [DiscoveryManager]
 *
 * @param lighthouseState A library-wide instance of [LighthouseState] to keep track of discovered devices
 * @param multicastSocketListener An implementation of [SocketListener] to send/receive packets from the network
 */
internal class RealDiscoveryManager(
    private val lighthouseState: LighthouseState,
    private val multicastSocketListener: SocketListener,
    private val logger: LighthouseLogger? = null
) : DiscoveryManager {

    override fun createNewDeviceFlow(searchRequest: SearchRequest): Flow<List<AbridgedMediaDevice>> {
        return multicastSocketListener.listenForPackets(searchRequest)
            .mapNotNull { DatagramPacketTransformer(it) }
            .mapNotNull { MediaPacketParser(it) }
            .map { lighthouseState.parseMediaPacket(it) }
    }

    override fun createStaleDeviceFlow(): Flow<List<AbridgedMediaDevice>> {
        return flow {
            while (currentCoroutineContext().isActive) {
                delay(1000)
                emit(lighthouseState.parseStaleDevices())
            }
        }
    }
}
