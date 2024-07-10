package com.ivanempire.lighthouse.models.devices

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * Represents a service that a media device supports
 *
 * @property serviceType The service type that is obtained from SSDP packets or from the XML endpoint
 */
interface MediaService {
    val serviceType: String
}

/**
 * A specific version of a [MediaService], populated exclusively from the XML description endpoint
 *
 * @param serviceType The service type that is obtained from the XML field
 * @param serviceId The service identifier
 * @param descriptionUrl The partial endpoint to call for the service description
 * @param controlUrl The partial endpoint to call for the service control
 * @param eventUrl The partial endpoint to call for service event subscriptions
 */
@Serializable
@XmlSerialName("service", "urn:schemas-upnp-org:device-1-0", "")
data class DetailedEmbeddedMediaService(
    @XmlElement(true)
    override val serviceType: String,
    @XmlElement(true)
    val serviceId: String,
    @XmlElement(true)
    @XmlSerialName("SCPDURL", "urn:schemas-upnp-org:device-1-0", "")
    val descriptionUrl: String,
    @XmlElement(true)
    @XmlSerialName("controlURL", "urn:schemas-upnp-org:device-1-0", "")
    val controlUrl: String,
    @XmlElement(true)
    @XmlSerialName("eventSubURL", "urn:schemas-upnp-org:device-1-0", "")
    val eventUrl: String,
) : MediaService
