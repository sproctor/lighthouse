package com.ivanempire.lighthouse.models.devices

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * Icon to depict device in a control point UI. Is allowed to have a
 * different value depending on language requested (see ACCEPT-LANGUAGE and
 * CONTENT-LANGUAGE header fields). Icon sizes to support are vendor-specific.
 * Contains the following sub elements:
 *
 * @param [mimetype] Icon's MIME type
 * @param [width]  Horizontal dimension of icon in pixels
 * @param [height] Vertical dimension of icon in pixels
 * @param [depth] Number of color bits per pixel
 * @param [url] Pointer to icon image
 */
@Serializable
@XmlSerialName("icon", "urn:schemas-upnp-org:device-1-0", "")
data class MediaIcon(
    @XmlElement(true)
    val mimetype: String,
    @XmlElement(true)
    val width: Int,
    @XmlElement(true)
    val height: Int,
    @XmlElement(true)
    val depth: Int,
    @XmlElement(true)
    val url: String,
)
