package com.mccartycarclub.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mccartycarclub.domain.UiUserMessage
import com.mccartycarclub.domain.model.DeviceContact
import com.mccartycarclub.domain.model.UserSearchResult
import com.mccartycarclub.repository.ContactType
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.CurrentContact
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.repository.ReceivedContactInvite
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.repository.SearchUser
import com.mccartycarclub.repository.SentInviteContactInvite
import com.mccartycarclub.ui.components.ContactCardConnectionEvent
import com.mccartycarclub.ui.viewmodels.ContactsViewModel.Companion.SEARCH_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.String
import kotlin.time.Duration.Companion.milliseconds

data class SearchUiState(
    val pending: Boolean = false,
    val idle: Boolean = true,
    val isSendingInvite: Boolean = false,
    val inviteSent: Boolean = false, // TODO: to remove no longer needed
    val message: UiUserMessage? = null,
    val searchResult: UserSearchResult? = null,
    val results: List<SearchUser> = emptyList(),
    val appUsers: List<DeviceContact> = emptyList(),
    val nonAppUsers: List<DeviceContact> = emptyList(),
    val deviceContacts: List<DeviceContact> = emptyList(),
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: RemoteRepo,
    private val contactsRepository: ContactsRepository,
) : ViewModel() {

    private val _query = MutableStateFlow<String?>(null)
    val query = _query.asStateFlow()

    var uiState by mutableStateOf(SearchUiState())
        private set

    init {
        viewModelScope.launch {
            uiState = uiState.copy(pending = true)
            val contactsWrapper = contactsRepository.getDeviceContacts()

            uiState = uiState.copy(
                pending = false,
                appUsers = contactsWrapper.appUsers,
                nonAppUsers = contactsWrapper.nonAppUsers,
            )

            userSearch()
        }
    }

    fun inviteSentEvent(connectionEvent: ContactCardConnectionEvent) {
        when (connectionEvent) {
            is ContactCardConnectionEvent.InviteConnectEvent -> {

                // TODO: push down into function
                val updatedResults = uiState.results.map { user ->
                    if (user.userId == connectionEvent.receiverUserId) {
                        user.copy(connectButtonEnabled = false)
                    } else {
                        user
                    }
                }

                uiState = uiState.copy(results = updatedResults)

                viewModelScope.launch {
                    uiState = uiState.copy(pending = true, isSendingInvite = true)
                    //val channelName =
                    //    ChannelModel.NotificationsDirect.getName(connectionEvent.receiverUserId)
                    // TODO: will uncomment when ready
                    //realtimePublishRepo.publish(channelName)
                    //realTime.createReceiverInviteSubscription(_userId.value.toString(), channel)

                    val data = repo.sendInviteToConnect(
                        receiverUserId = connectionEvent.receiverUserId,
                        rowId = connectionEvent.rowId,
                    ).first()

                    uiState = when (data) {
                        is NetworkResponse.Error -> {
                            uiState.copy(message = UiUserMessage.NETWORK_ERROR)
                        }

                        NetworkResponse.NoInternet -> {
                            uiState.copy(message = UiUserMessage.NO_INTERNET)
                        }

                        is NetworkResponse.Success -> {
                            uiState.copy(pending = false, message = UiUserMessage.INVITE_SENT)
                        }
                    }
                }
            }

            is ContactCardConnectionEvent.InvitePhoneNumberConnectEvent -> {
                disableButton(connectionEvent.receiverPhoneNumber)

                viewModelScope.launch {
                    repo.sendPhoneNumberInviteToConnect(
                        phoneNumber = connectionEvent.receiverPhoneNumber
                    ).collect { response ->
                        uiState = when (response) {
                            is NetworkResponse.Error -> {
                                uiState.copy(message = UiUserMessage.NETWORK_ERROR)
                            }

                            NetworkResponse.NoInternet -> {
                                uiState.copy(message = UiUserMessage.NO_INTERNET)
                            }

                            is NetworkResponse.Success -> {
                                uiState.copy(
                                    pending = false,
                                    message = UiUserMessage.INVITE_SENT
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun onQueryChange(searchQuery: String) {
        _query.value = searchQuery
        uiState = uiState.copy(results = emptyList())
    }

    suspend fun userSearch() {
        query.debounce(SEARCH_DELAY.milliseconds).distinctUntilChanged().collectLatest { userName ->
            if (!userName.isNullOrEmpty()) {
                uiState = uiState.copy(pending = true)

                val userSearch = repo.searchUsersByUserName(userName = userName)

                val contacts = when (val result = repo.fetchAllContacts().first()) {
                    is NetworkResponse.Success -> {
                        result.data ?: emptyList()
                    }

                    else -> {
                        emptyList()
                    }
                }

                val searchUsers = when (val result = userSearch.first()) {
                    is NetworkResponse.Error -> {
                        uiState.copy(message = UiUserMessage.NETWORK_ERROR)
                        emptyList()
                    }

                    NetworkResponse.NoInternet -> {
                        uiState.copy(message = UiUserMessage.NO_INTERNET)
                        emptyList()
                    }

                    is NetworkResponse.Success -> {
                        result.data ?: emptyList()
                    }
                }

                val userType: List<SearchUser> = searchUsers.map { user ->
                    val contact = contacts.find {
                        it.userId == user.userId
                    }
                    val contactType = when (contact) {
                        is ReceivedContactInvite -> ContactType.RECEIVED
                        is SentInviteContactInvite -> ContactType.SENT
                        is CurrentContact -> ContactType.CURRENT
                        else -> null
                    }
                    SearchUser(
                        id = user.id,
                        userName = user.userName,
                        avatarUri = user.avatarUri,
                        userId = user.userId,
                        phone = user.phone,
                        connectButtonEnabled = true,
                        contactType = contactType,
                    )
                }

                enableButton(userType)

            } else {
                uiState = uiState.copy(idle = true)
            }
        }
    }

    fun disableButton(phoneNumber: String) {
        val appUsers = uiState.appUsers.map {
            if (it.phoneNumbers.any { phone -> phone == phoneNumber }) {
               // it.copy(connectButtonEnabled = false)
            } else {
                it
            }
        }
        //uiState = uiState.copy(appUsers = appUsers)
    }

    private fun enableButton(users: List<SearchUser>) {
        val buttonsEnabled = users.map {
            it.copy(connectButtonEnabled = true)
        }

        uiState = uiState.copy(
            results = buttonsEnabled,
            pending = false,
            inviteSent = false,
            isSendingInvite = false,
        )
    }
}
