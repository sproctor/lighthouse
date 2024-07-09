package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.LighthouseLogger

/** Specific implementation of [SocketListener] */
internal class JvmSocketListener(
    retryCount: Int,
    logger: LighthouseLogger? = null,
) : BaseSocketListener(retryCount, logger) {

    override fun acquireMulticastLock() {
        // Nothing to do on JVM
    }

    override fun releaseMulticastLock() {
        // Nothing to do on JVM
    }
}
