package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.ByeByeMediaPacket
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_NUM
import com.ivanempire.lighthouse.models.HeaderKeys
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.MediaPacket
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName

class ByeByeMediaPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val host: MediaHost by lazy {
        MediaHost.parseFromString(headerSet[HeaderKeys.HOST] ?: NOT_AVAILABLE)
    }

    private val notificationType: NotificationType by lazy {
        NotificationType(headerSet[HeaderKeys.NOTIFICATION_TYPE] ?: NOT_AVAILABLE)
    }

    private val uniqueServiceName: UniqueServiceName by lazy {
        UniqueServiceName(headerSet[HeaderKeys.UNIQUE_SERVICE_NAME] ?: NOT_AVAILABLE)
    }

    private val bootId = headerSet[HeaderKeys.BOOTID]?.toInt() ?: NOT_AVAILABLE_NUM

    private val configId = headerSet[HeaderKeys.CONFIGID]?.toInt() ?: NOT_AVAILABLE_NUM

    override fun parseMediaPacket(): MediaPacket {
        return ByeByeMediaPacket(
            host = host,
            notificationType = notificationType,
            usn = uniqueServiceName,
            bootId = bootId,
            configId = configId,
            uuid = parseIdentifier(notificationType, uniqueServiceName)
        )
    }
}
