package com.cerqa.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cerqa.domain.repository.LocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Shared ViewModel for main app state
 * Used by both Android and iOS
 */
class SharedMainViewModel(
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserData()
    }

    /**
     * Load user data from local storage
     */
    private fun loadUserData() {
        viewModelScope.launch {
            _userId.value = localRepository.getUserId().first()
            _userName.value = localRepository.getUserName().first()
        }
    }

    /**
     * Set the logged in user ID
     * Called after successful authentication
     */
    fun setLoggedInUserId(userId: String) {
        viewModelScope.launch {
            localRepository.setLocalUserId(userId)
            _userId.value = userId
        }
    }

    /**
     * Set user data (ID and name)
     * Called after successful authentication
     */
    fun setUserData(userId: String, userName: String) {
        viewModelScope.launch {
            localRepository.setUserData(userId, userName)
            _userId.value = userId
            _userName.value = userName
        }
    }

    /**
     * Clear user data on sign out
     */
    fun clearUserData() {
        viewModelScope.launch {
            localRepository.clearUserData()
            _userId.value = null
            _userName.value = null
        }
    }

    /**
     * Refresh user data from local storage
     */
    fun refreshUserData() {
        loadUserData()
    }
}
