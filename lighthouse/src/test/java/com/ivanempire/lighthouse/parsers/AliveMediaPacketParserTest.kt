package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.AliveMediaPacket
import com.ivanempire.lighthouse.models.MediaHost
import com.ivanempire.lighthouse.models.NotificationType
import com.ivanempire.lighthouse.models.UniqueServiceName
import com.ivanempire.lighthouse.parsers.AliveMediaPacketParserTest.Fixtures.COMPLETE_PACKET
import com.ivanempire.lighthouse.parsers.AliveMediaPacketParserTest.Fixtures.INCOMPLETE_PACKET
import java.net.InetAddress
import java.net.URL
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class AliveMediaPacketParserTest {

    private lateinit var sut: AliveMediaPacketParser

    @Test
    fun `parses complete packet correctly`() {
        sut = AliveMediaPacketParser(COMPLETE_PACKET)
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("239.255.255.250"), 1900), parsedPacket.host)
        assertEquals(1800, parsedPacket.cache)
        assertEquals(
            URL("http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"),
            parsedPacket.location
        )
        assertEquals("Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5", parsedPacket.server)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:ConnectionManager:1"),
            parsedPacket.notificationType
        )
        assertEquals(
            UniqueServiceName(
                "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:ConnectionManager:1"
            ),
            parsedPacket.usn
        )
        assertEquals(245, parsedPacket.bootId)
        assertEquals(564, parsedPacket.configId)
        assertEquals(1900, parsedPacket.searchPort)
        assertEquals(UUID.fromString("b9783ad2-d548-9793-0eb9-42db373ade07"), parsedPacket.uuid)
    }

    @Test
    fun `parses incomplete packet correctly`() {
        sut = AliveMediaPacketParser(INCOMPLETE_PACKET)
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("127.0.0.1"), -1), parsedPacket.host)
        assertEquals(1800, parsedPacket.cache)
        assertEquals(
            URL("http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml"),
            parsedPacket.location
        )
        assertEquals("N/A", parsedPacket.server)
        assertEquals(
            NotificationType("urn:schemas-upnp-org:service:ConnectionManager:1"),
            parsedPacket.notificationType
        )
        assertEquals(UniqueServiceName("N/A"), parsedPacket.usn)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(1900, parsedPacket.searchPort)
        assertEquals(UUID(0, 0), parsedPacket.uuid)
    }

    @Test
    fun `parses empty packet correctly`() {
        sut = AliveMediaPacketParser(hashMapOf())
        val parsedPacket = sut.parseMediaPacket() as AliveMediaPacket

        assertEquals(MediaHost(InetAddress.getByName("127.0.0.1"), -1), parsedPacket.host)
        assertEquals(-1, parsedPacket.cache)
        assertEquals(URL("http://127.0.0.1/"), parsedPacket.location)
        assertEquals("N/A", parsedPacket.server)
        assertEquals(NotificationType("N/A"), parsedPacket.notificationType)
        assertEquals(UniqueServiceName("N/A"), parsedPacket.usn)
        assertEquals(-1, parsedPacket.bootId)
        assertEquals(-1, parsedPacket.configId)
        assertEquals(-1, parsedPacket.searchPort)
        assertEquals(UUID(0, 0), parsedPacket.uuid)
    }

    object Fixtures {
        val COMPLETE_PACKET = hashMapOf(
            "HOST" to "239.255.255.250:1900",
            "CACHE-CONTROL" to "max-age=1800",
            "LOCATION" to "http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml",
            "SERVER" to "Linux/3.18.71+ UPnP/1.0 GUPnP/1.0.5",
            "NTS" to "ssdp:alive",
            "NT" to "urn:schemas-upnp-org:service:ConnectionManager:1",
            "USN" to "uuid:b9783ad2-d548-9793-0eb9-42db373ade07::urn:schemas-upnp-org:service:ConnectionManager:1",
            "BOOTID.UPNP.ORG" to "245",
            "CONFIGID.UPNP.ORG" to "564",
            "SEARCHPORT.UPNP.ORG" to "1900"
        )

        val INCOMPLETE_PACKET = hashMapOf(
            "CACHE-CONTROL" to "max-age=1800",
            "LOCATION" to "http://192.168.1.190:8091/b9783ad2-d548-9793-0eb9-42db373ade07.xml",
            "NT" to "urn:schemas-upnp-org:service:ConnectionManager:1",
            "SEARCHPORT.UPNP.ORG" to "1900"
        )
    }
}
