package com.ivanempire.lighthouse.socket

import android.net.wifi.WifiManager
import com.ivanempire.lighthouse.LighthouseLogger
import com.ivanempire.lighthouse.models.Constants.LIGHTHOUSE_CLIENT

/** Specific implementation of [SocketListener] */
internal class AndroidSocketListener(
    private val wifiManager: WifiManager,
    retryCount: Int,
    logger: LighthouseLogger? = null,
) : BaseSocketListener(retryCount, logger) {

    private val multicastLock: WifiManager.MulticastLock by lazy {
        wifiManager.createMulticastLock(LIGHTHOUSE_CLIENT)
    }

    override fun acquireMulticastLock() {
        multicastLock.setReferenceCounted(true)
        multicastLock.acquire()
    }

    override fun releaseMulticastLock() {
        if (multicastLock.isHeld) {
            multicastLock.release()
        }
    }
}
