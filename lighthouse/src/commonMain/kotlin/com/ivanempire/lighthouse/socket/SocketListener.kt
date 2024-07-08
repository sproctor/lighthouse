package com.ivanempire.lighthouse.socket

import com.ivanempire.lighthouse.models.search.SearchRequest
import java.net.DatagramPacket
import java.net.MulticastSocket
import kotlinx.coroutines.flow.Flow

/**
 * All socket listeners should conform to this interface in order to set up their sockets, listen
 * for network packets, and release their resources once discovery has stopped
 */
internal interface SocketListener {

    /**
     * Sends a [SearchRequest] to the multicast socket and starts listening for packets that come
     * back from the multicast group. Emits each response via a [DatagramPacket] in a Kotlin flow
     *
     * @param searchRequest The [SearchRequest] instance to send to the multicast group
     * @return Flow of [DatagramPacket] - each emission represents a single received packet
     */
    fun listenForPackets(searchRequest: SearchRequest): Flow<DatagramPacket>
}
