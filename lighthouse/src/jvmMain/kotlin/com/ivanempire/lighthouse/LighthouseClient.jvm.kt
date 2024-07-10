package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.core.RealLighthouseClient
import com.ivanempire.lighthouse.socket.JvmSocketListener
import io.ktor.client.HttpClient

fun lighthouseClient(init: LighthouseClient.Builder.() -> Unit = {}): LighthouseClient {
    val builder =
        object : LighthouseClient.Builder {
            override var retryCount = 0

            override var logger: LighthouseLogger? = null

            override var httpClient: HttpClient = HttpClient()
        }

    builder.init()

    require(builder.retryCount >= 0) { "Retry count must be greater than or equal to 0" }

    val socketListener = JvmSocketListener(builder.retryCount, builder.logger)

    return RealLighthouseClient(
        socketListener = socketListener,
        httpClient = builder.httpClient,
        logger = builder.logger,
    )
}
