package com.cerqa.domain.repository

import com.cerqa.domain.model.User
import com.cerqa.domain.model.NetworkResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user data
 * Platform-specific implementations will use Amplify Android SDK or Amplify Swift SDK
 */
interface UserRepository {
    /**
     * Get the currently authenticated user
     */
    suspend fun getCurrentUser(): NetworkResponse<User>

    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: String): NetworkResponse<User>

    /**
     * Get the current user ID from local storage
     */
    fun getCurrentUserId(): Flow<String?>

    /**
     * Check if user is authenticated
     */
    suspend fun isUserAuthenticated(): Boolean

    /**
     * Sign out the current user
     */
    suspend fun signOut()
}
