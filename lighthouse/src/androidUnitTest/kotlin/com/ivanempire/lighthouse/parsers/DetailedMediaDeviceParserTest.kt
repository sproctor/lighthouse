package com.ivanempire.lighthouse.parsers

import org.junit.Assert.assertEquals
import org.junit.Test

/** Tests [DetailedMediaDeviceParser] */
class DetailedMediaDeviceParserTest {
    @Test
    fun `tplink device with services correctly parses`() {
        val xmlInput = javaClass.classLoader.getResource("tplink.xml").readText()
        val device = DetailedMediaDeviceParser.parse(xmlInput)
        assertEquals("Archer AX50", device.friendlyName)
        assertEquals("uuid:759c0438-56dc-459a-8e75-8d34b9a5077f", device.udn)
        assertEquals(1, device.serviceList?.size)
    }

    @Test
    fun `nvidia shield description parses`() {
        val xmlInput = javaClass.classLoader.getResource("nvidia.xml").readText()
        val device = DetailedMediaDeviceParser.parse(xmlInput)
        assertEquals("SHIELD", device.friendlyName)
        assertEquals("uuid:8544cd4e-7256-45ac-b825-eafb91f62d29", device.udn)
    }

    @Test
    fun `netgear description parses`() {
        val xmlInput = javaClass.classLoader.getResource("netgear.xml").readText()
        val device = DetailedMediaDeviceParser.parse(xmlInput)
        assertEquals("R6230 (Gateway)", device.friendlyName)
        assertEquals("uuid:824ff22b-8c7d-41c5-a131-78d29484ac56", device.udn)
    }
}
