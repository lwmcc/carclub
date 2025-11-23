package com.cerqa.domain.repository

import com.cerqa.domain.model.Contact
import com.cerqa.domain.model.ContactsWrapper
import com.cerqa.domain.model.NetDeleteResult
import com.cerqa.domain.model.NetworkResponse
import com.cerqa.domain.model.SearchContact
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing contacts
 * Platform-specific implementations will use Amplify Android SDK or Amplify Swift SDK
 */
interface ContactsRepository {
    /**
     * Create a contact between sender and logged in user
     */
    fun createContact(senderUserId: String, loggedInUserId: String): Flow<NetDeleteResult>

    /**
     * Check if a contact exists between two users
     */
    fun contactExists(senderUserId: String, receiverUserId: String): Flow<Boolean>

    /**
     * Fetch all contacts for the current user
     * Includes received invites, sent invites, and confirmed contacts
     */
    fun fetchAllContacts(): Flow<NetworkResponse<List<Contact>>>

    /**
     * Fetch users by phone number from the backend
     * Returns a pair of app users and non-app users
     */
    suspend fun fetchUsersByPhoneNumber(): Pair<List<SearchContact>, List<SearchContact>>

    /**
     * Get contacts from the device
     */
    suspend fun getDeviceContacts(): ContactsWrapper

    /**
     * Create a contact from an Amplify User model
     * This will be platform-specific in implementation
     */
    suspend fun createContactFromUser(userId: String)
}
