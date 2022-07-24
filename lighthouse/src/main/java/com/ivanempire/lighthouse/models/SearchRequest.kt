package com.ivanempire.lighthouse.models

import com.ivanempire.lighthouse.models.Constants.DEFAULT_SEARCH_MAN
import com.ivanempire.lighthouse.models.Constants.FIELD_SEPARATOR
import com.ivanempire.lighthouse.models.packets.HeaderKeys
import com.ivanempire.lighthouse.models.packets.MediaHost
import com.ivanempire.lighthouse.models.packets.StartLine
import java.lang.StringBuilder
import java.net.DatagramPacket
import java.net.InetAddress
import java.util.UUID

/**
 * All SSDP search requests conform to this interface in order to be passed around in the library
 */
interface SearchRequest {

    /**
     * Converts a specific implementation to a [DatagramPacket] in order to be sent along the wire
     *
     * @param startLine The packet [StartLine], defaults to [StartLine.SEARCH]
     *
     * @return The [DatagramPacket] representing this search request, [StartLine] included
     */
    fun toDatagramPacket(multicastGroup: InetAddress): DatagramPacket
}

/**
 * @param hostname Required - [MediaHost]
 * @param mx Required - seconds by which to delay the search response, between 1 and 5, inclusive
 * @param searchTarget Required - search target to use for the search request
 * @param osVersion Allowed - OS version for the user agent field
 * @param productVersion Allowed - product version for the user agent field
 * @param friendlyName Required - friendly name of the control point to use
 * @param uuid Allowed - UUID of the control point to use
 */
data class MulticastSearchRequest(
    val hostname: MediaHost,
    val mx: Int,
    val searchTarget: String,
    val osVersion: String?,
    val productVersion: String?,
    val friendlyName: String = "LighthouseClient",
    val uuid: UUID = UUID.nameUUIDFromBytes("LighthouseClient".toByteArray())
) : SearchRequest {

    val NEWLINE_SEPARATOR = "\r\n"

    init {
        require(mx in 1..5) {
            "MX should be between 1 and 5 inclusive"
        }
        require(searchTarget.isNotEmpty()) {
            "Search target (ST) should not be an empty string"
        }
        require(friendlyName.isNotEmpty()) {
            "Friendly name (CPFN.UPNP.ORG) should not be an empty string"
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append(StartLine.SEARCH.rawString).append(NEWLINE_SEPARATOR)
        builder.append(HeaderKeys.HOST).append(FIELD_SEPARATOR).append(hostname.toString()).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.MAN).append(FIELD_SEPARATOR).append(DEFAULT_SEARCH_MAN).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.MX).append(FIELD_SEPARATOR).append(mx).append(NEWLINE_SEPARATOR)
            .append(HeaderKeys.SEARCH_TARGET).append(FIELD_SEPARATOR).append(searchTarget).append(NEWLINE_SEPARATOR)

        if (!osVersion.isNullOrEmpty() && !productVersion.isNullOrEmpty()) {
            builder.append(HeaderKeys.USER_AGENT).append(FIELD_SEPARATOR).append("$osVersion UPnP/2.0 $productVersion").append(NEWLINE_SEPARATOR)
        }

        builder.append(HeaderKeys.FRIENDLY_NAME).append(FIELD_SEPARATOR).append(friendlyName).append(NEWLINE_SEPARATOR)
        builder.append(HeaderKeys.CONTROL_POINT_UUID).append(FIELD_SEPARATOR).append(uuid).append(NEWLINE_SEPARATOR)

        return builder.toString()
    }

    override fun toDatagramPacket(multicastGroup: InetAddress): DatagramPacket {
        val searchByteArray = "${StartLine.SEARCH}\n${toString()}".toByteArray()
        return DatagramPacket(searchByteArray, searchByteArray.size, multicastGroup, 1900)
    }
}
