package com.ivanempire.lighthouse.core

import com.ivanempire.lighthouse.LighthouseClient
import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.DetailedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import com.ivanempire.lighthouse.parsers.DatagramPacketTransformer
import com.ivanempire.lighthouse.parsers.DetailedMediaDeviceParser
import com.ivanempire.lighthouse.parsers.packets.MediaPacketParser
import com.ivanempire.lighthouse.socket.SocketListener
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive

/** Specific implementation of [LighthouseClient] */
internal class RealLighthouseClient(
    private val socketListener: SocketListener,
    private val logger: LighthouseLogger?,
) : LighthouseClient {

    private val httpClient = HttpClient()

    override suspend fun discoverDevices(
        searchRequest: SearchRequest,
    ): Flow<List<AbridgedMediaDevice>> {
        val state = LighthouseState(logger)

        logger?.logStatusMessage(TAG, "Discovering devices with search request: $searchRequest")

        val foundDevicesFlow = createNewDeviceFlow(searchRequest, state)
        val withoutStaleDevicesFlow = createNonStaleDeviceFlow(state)

        return merge(foundDevicesFlow, withoutStaleDevicesFlow).distinctUntilChanged()
    }

    override suspend fun retrieveDescription(abridgedMediaDevice: AbridgedMediaDevice): Result<DetailedMediaDevice> {
        return try {
            // TODO: use secure location if it is provided
            val response = httpClient.get(abridgedMediaDevice.location)
            Result.success(DetailedMediaDeviceParser.parse(response.body()))
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun createNewDeviceFlow(
        searchRequest: SearchRequest,
        lighthouseState: LighthouseState,
    ): Flow<List<AbridgedMediaDevice>> {
        return socketListener
            .listenForPackets(searchRequest)
            .mapNotNull { DatagramPacketTransformer(it, logger) }
            .mapNotNull { MediaPacketParser(it, logger) }
            .onEach { lighthouseState.parseMediaPacket(it) }
            .flatMapLatest { lighthouseState.deviceList }
            .filter { it.isNotEmpty() }
    }

    private fun createNonStaleDeviceFlow(
        lighthouseState: LighthouseState
    ): Flow<List<AbridgedMediaDevice>> {
        return flow {
                while (currentCoroutineContext().isActive) {
                    delay(1000)
                    lighthouseState.removeStaleDevices()
                    emit(lighthouseState.deviceList.value)
                }
            }
            .filter { it.isNotEmpty() }
    }

    private companion object {
        const val TAG = "RealLighthouseClient"
    }
}
