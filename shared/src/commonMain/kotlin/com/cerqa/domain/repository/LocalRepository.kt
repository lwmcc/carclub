package com.cerqa.domain.repository

import com.cerqa.domain.model.LocalDeviceContacts
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing local data and preferences
 * Platform-specific implementations will use DataStore (Android) or UserDefaults (iOS)
 */
interface LocalRepository {
    /**
     * Get the currently logged in user ID from local storage
     */
    fun getUserId(): Flow<String?>

    /**
     * Get all contacts from the device's contact list
     */
    suspend fun getAllContacts(): List<LocalDeviceContacts>

    /**
     * Save the user ID to local storage
     */
    suspend fun setLocalUserId(userId: String)

    /**
     * Get the user name from local storage
     */
    fun getUserName(): Flow<String?>

    /**
     * Save user data to local storage
     */
    suspend fun setUserData(userId: String, userName: String)

    /**
     * Clear all local user data
     */
    suspend fun clearUserData()
}
