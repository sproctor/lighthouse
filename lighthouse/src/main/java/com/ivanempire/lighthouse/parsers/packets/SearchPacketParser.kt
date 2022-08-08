package com.ivanempire.lighthouse.parsers.packets

import com.ivanempire.lighthouse.getAndRemove
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE
import com.ivanempire.lighthouse.models.Constants.NOT_AVAILABLE_NUM
import com.ivanempire.lighthouse.models.devices.MediaDeviceServer
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaPacket
import com.ivanempire.lighthouse.models.packets.NotificationType
import com.ivanempire.lighthouse.models.packets.SearchResponseMediaPacket
import com.ivanempire.lighthouse.models.packets.UniqueServiceName
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.HashMap

class SearchPacketParser(
    private val headerSet: HashMap<String, String>
) : MediaPacketParser() {

    private val cacheControl: Int by lazy {
        parseCacheControl(headerSet.getAndRemove(HeaderKeys.CACHE_CONTROL))
    }

    private val date: Date by lazy {
        // TODO: Use proper date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        try {
            dateFormat.parse(headerSet.getAndRemove(HeaderKeys.DATE) ?: NOT_AVAILABLE)
        } catch (ex: ParseException) {
            Date()
        }
    }

    private val location: URL by lazy {
        parseUrl(headerSet.getAndRemove(HeaderKeys.LOCATION))
    }

    private val server: MediaDeviceServer by lazy {
        MediaDeviceServer.parseFromString(headerSet.getAndRemove(HeaderKeys.SERVER))
    }

    private val notificationType: NotificationType by lazy {
        NotificationType(headerSet.getAndRemove(HeaderKeys.SEARCH_TARGET))
    }

    private val uniqueServiceName: UniqueServiceName by lazy {
        UniqueServiceName(headerSet.getAndRemove(HeaderKeys.UNIQUE_SERVICE_NAME) ?: "", bootId)
    }

    private val bootId = headerSet.getAndRemove(HeaderKeys.BOOT_ID)?.toInt() ?: NOT_AVAILABLE_NUM

    private val configId = headerSet.getAndRemove(HeaderKeys.CONFIG_ID)?.toInt() ?: NOT_AVAILABLE_NUM

    private val searchPort = headerSet.getAndRemove(HeaderKeys.SEARCH_PORT)?.toInt() ?: NOT_AVAILABLE_NUM

    private val secureLocation: URL by lazy {
        parseUrl(headerSet.getAndRemove(HeaderKeys.SECURE_LOCATION))
    }

    override fun parseMediaPacket(): MediaPacket {
        return SearchResponseMediaPacket(
            cache = cacheControl,
            date = date,
            location = location,
            server = server,
            usn = uniqueServiceName,
            notificationType = notificationType,
            bootId = bootId,
            configId = configId,
            searchPort = searchPort,
            secureLocation = secureLocation
        )
    }
}
