package com.ivanempire.lighthouse

import com.ivanempire.lighthouse.models.devices.AbridgedMediaDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedDevice
import com.ivanempire.lighthouse.models.packets.EmbeddedService
import com.ivanempire.lighthouse.models.packets.UniqueServiceName

/**
 * Handles the latest [UniqueServiceName] instance from an ALIVE packet to update an embedded
 * component - be that a device or a service. We replace the entire component because versions may
 * change in the case of an UPDATE packet.
 *
 * @param latestComponent The latest ALIVE or BYEBYE packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.updateEmbeddedComponent(latestComponent: UniqueServiceName) {
    if (latestComponent is EmbeddedDevice) {
        val targetComponent = this.deviceList.firstOrNull { it.deviceType == latestComponent.deviceType }
        this.deviceList.remove(targetComponent)
        this.deviceList.add(latestComponent)
    } else if (latestComponent is EmbeddedService) {
        val targetComponent = this.serviceList.firstOrNull { it.serviceType == latestComponent.serviceType }
        this.serviceList.remove(targetComponent)
        this.serviceList.add(latestComponent)
    }
}

/**
 * Handles the latest [UniqueServiceName] instance from a BYEBYE packet to remove an embedded
 * component - be that a device or a service - from the root device
 *
 * @param latestComponent The latest BYEBYE packet's parsed [UniqueServiceName] field
 */
internal fun AbridgedMediaDevice.removeEmbeddedComponent(latestComponent: UniqueServiceName) {
    if (latestComponent is EmbeddedDevice) {
        this.deviceList.removeAll { it.deviceType == latestComponent.deviceType }
    } else if (latestComponent is EmbeddedService) {
        this.serviceList.removeAll { it.serviceType == latestComponent.serviceType }
    }
}

/**
 * Returns and removes an entry from the receiving [HashMap]. This is used to remove all standard
 * fields from an SSDP packet, so that the only ones left at the end of parsing would be the extra
 * headers specified by the manufacturer.
 *
 * @param targetKey The key of the target value to look up, remove, and return from the [HashMap]
 * @return The lookup value from [targetKey], null otherwise
 */
internal fun HashMap<String, String>.getAndRemove(targetKey: String): String? {
    val targetValue = this[targetKey]
    this.remove(targetKey)
    return targetValue
}
