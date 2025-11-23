package com.cerqa.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cerqa.domain.model.Contact
import com.cerqa.domain.model.NetworkResponse
import com.cerqa.domain.repository.ContactsRepository
import com.cerqa.domain.repository.LocalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * UI State for contacts screen
 */
data class ContactsUiState(
    val isLoading: Boolean = false,
    val contacts: List<Contact> = emptyList(),
    val errorMessage: String? = null,
    val hasNoInternet: Boolean = false
)

/**
 * Shared ViewModel for contacts management
 * Used by both Android and iOS
 */
class SharedContactsViewModel(
    private val contactsRepository: ContactsRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        loadUserId()
    }

    private fun loadUserId() {
        viewModelScope.launch {
            _userId.value = localRepository.getUserId().first()
        }
    }

    /**
     * Fetch all contacts for the current user
     */
    fun fetchContacts() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            when (val result = contactsRepository.fetchAllContacts().first()) {
                is NetworkResponse.Success -> {
                    _uiState.value = ContactsUiState(
                        isLoading = false,
                        contacts = result.data ?: emptyList(),
                        errorMessage = null,
                        hasNoInternet = false
                    )
                }

                is NetworkResponse.Error -> {
                    _uiState.value = ContactsUiState(
                        isLoading = false,
                        contacts = emptyList(),
                        errorMessage = result.exception.message ?: "Unknown error",
                        hasNoInternet = false
                    )
                }

                NetworkResponse.NoInternet -> {
                    _uiState.value = ContactsUiState(
                        isLoading = false,
                        contacts = emptyList(),
                        errorMessage = null,
                        hasNoInternet = true
                    )
                }
            }
        }
    }

    /**
     * Refresh contacts list
     */
    fun refreshContacts() {
        fetchContacts()
    }

    /**
     * Check if a contact exists
     */
    fun checkContactExists(receiverUserId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = _userId.value ?: return@launch
            val exists = contactsRepository.contactExists(userId, receiverUserId).first()
            onResult(exists)
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Get contacts for chat (non-blocking)
     */
    fun getContactsForChat(): List<Contact> {
        return _uiState.value.contacts
    }
}
