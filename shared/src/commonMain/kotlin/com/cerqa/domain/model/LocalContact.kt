package com.cerqa.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a contact from the device's local contact list
 */
@Serializable
data class LocalDeviceContacts(
    val name: String,
    val phoneNumbers: List<String> = emptyList(),
    val photoUri: String? = null,
    val thumbnailUri: String? = null
)
