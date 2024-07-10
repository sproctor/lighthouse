package com.ivanempire.lighthouse.parsers

import com.ivanempire.lighthouse.models.devices.DetailedMediaDevice
import com.ivanempire.lighthouse.models.devices.RootContainer
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.serialization.XML

object DetailedMediaDeviceParser {
    private val xml = XML()

    private val serializer = serializer<RootContainer>()

    fun parse(input: String): DetailedMediaDevice {
        return xml.decodeFromString(serializer, input).device
    }
}
