package com.cerqa.domain.model

import kotlinx.serialization.Serializable

/**
 * Base contact model
 * Note: Using abstract class without @Serializable to avoid serialization conflicts
 */
abstract class Contact {
    abstract val contactId: String
    abstract val userId: String
    abstract val userName: String
    abstract val name: String
    abstract val avatarUri: String
    abstract val createdAt: String
    abstract val phoneNumber: String
}

/**
 * Standard contact (confirmed connection)
 */
@Serializable
data class StandardContact(
    override val contactId: String,
    override val userId: String,
    override val userName: String,
    override val name: String,
    override val avatarUri: String,
    override val createdAt: String,
    override val phoneNumber: String,
) : Contact()

/**
 * Represents a contact invitation that was received
 */
@Serializable
data class ReceivedContactInvite(
    override val contactId: String,
    override val userId: String,
    override val userName: String,
    override val name: String,
    override val avatarUri: String,
    override val createdAt: String,
    override val phoneNumber: String,
) : Contact()

/**
 * Represents a contact invitation that was sent
 */
@Serializable
data class SentContactInvite(
    val senderUserId: String,
    override val contactId: String,
    override val userId: String,
    override val userName: String,
    override val name: String,
    override val avatarUri: String,
    override val createdAt: String,
    override val phoneNumber: String,
) : Contact()

/**
 * Wrapper for device contacts categorized by app usage
 */
@Serializable
data class ContactsWrapper(
    val appUsers: List<DeviceContact>,
    val nonAppUsers: List<DeviceContact>,
)

/**
 * Represents a contact from the device contact list
 */
@Serializable
data class DeviceContact(
    val name: String,
    val phoneNumber: String,
    val isAppUser: Boolean = false
)

/**
 * Represents a search contact result
 */
@Serializable
data class SearchContact(
    val userId: String,
    val userName: String,
    val name: String,
    val phone: String,
    val avatarUri: String? = null
)
