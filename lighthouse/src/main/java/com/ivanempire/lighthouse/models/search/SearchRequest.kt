package com.ivanempire.lighthouse.models.search

import com.ivanempire.lighthouse.models.packets.StartLine
import java.net.DatagramPacket
import java.net.InetAddress

/**
 * All SSDP search requests conform to this interface for Lighthouse to use
 */
interface SearchRequest {

    /**
     * Converts a search packet into a [DatagramPacket] to send to the multicast group. Unless
     * something special is required, the only required parameter is the multicast group address.
     *
     * @param multicastAddress The [InetAddress] of the multicast group
     * @return The search request in [DatagramPacket] form
     */
    fun toDatagramPacket(multicastAddress: InetAddress): DatagramPacket {
        val searchByteArray = "${StartLine.SEARCH}\n${toString()}".toByteArray()
        return DatagramPacket(searchByteArray, searchByteArray.size, multicastAddress, 1900)
    }
}
