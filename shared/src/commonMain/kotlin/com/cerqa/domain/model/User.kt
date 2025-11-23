package com.cerqa.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a user in the system
 */
@Serializable
data class User(
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val name: String,
    val phone: String,
    val userName: String,
    val email: String,
    val avatarUri: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Simple user data for preferences/local storage
 */
@Serializable
data class UserData(
    val userId: String?,
    val userName: String?
)
