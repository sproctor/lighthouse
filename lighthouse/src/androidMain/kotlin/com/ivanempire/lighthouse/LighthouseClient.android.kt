package com.ivanempire.lighthouse

import android.content.Context
import android.net.wifi.WifiManager
import com.ivanempire.lighthouse.core.LighthouseState
import com.ivanempire.lighthouse.core.RealDiscoveryManager
import com.ivanempire.lighthouse.core.RealLighthouseClient
import com.ivanempire.lighthouse.socket.AndroidSocketListener

fun lighthouseClient(
    context: Context,
    init: LighthouseClient.Builder.() -> Unit = {}
): LighthouseClient {
    val builder = object : LighthouseClient.Builder {
        override var retryCount = 0

        override var shouldPersist = false

        override var logger: LighthouseLogger? = null
    }


    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    builder.init()

    require(builder.retryCount >= 0) { "Retry count must be greater than or equal to 0" }

    val socketListener = AndroidSocketListener(wifiManager, builder.retryCount, builder.logger)

    val discoveryManager =
        RealDiscoveryManager(
            builder.shouldPersist,
            LighthouseState(builder.logger),
            socketListener,
            builder.logger,
        )
    return RealLighthouseClient(discoveryManager, logger = builder.logger)
}
