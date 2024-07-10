package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_REQUEST
import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.devices.DetailedMediaDevice
import com.ivanempire.lighthouse.models.search.SearchRequest
import kotlinx.coroutines.flow.Flow

/** The main entrypoint for the Lighthouse library */
interface LighthouseClient {

    /** Builder class for the Lighthouse configuration */
    interface Builder {
        /**
         * Specify a retry count in the off-chance the network packet is not received by the
         * multicast group due to the nature of UDP
         *
         * @param retryCount Number of times to retry sending an SSDP search packet, must be > 0
         */
        var retryCount: Int

        /**
         * Specify a custom implementation of [LighthouseLogger] in order to log events from the
         * library at the consumer level
         *
         * @param logger Custom implementation of [LighthouseLogger]
         */
        var logger: LighthouseLogger?
    }

    /**
     * When called, this method will start device discovery by kicking off all of the necessary
     * setup steps down to the socket listener
     *
     * @param searchRequest The [SearchRequest] to send to the multicast group to discover devices
     * @return Flow of lists of [AbridgedMediaDevice] that have been discovered on the network
     */
    suspend fun discoverDevices(
        searchRequest: SearchRequest = DEFAULT_SEARCH_REQUEST
    ): Flow<List<AbridgedMediaDevice>>

    suspend fun retrieveDescription(abridgedMediaDevice: AbridgedMediaDevice): Result<DetailedMediaDevice>
}
