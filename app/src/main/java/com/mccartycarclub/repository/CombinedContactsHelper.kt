package com.mccartycarclub.repository

import kotlinx.coroutines.flow.Flow

interface CombinedContactsHelper {
    fun fetchAllContacts(loggedInUserId: String): Flow<NetworkResponse<List<Contact>>>
    fun fetchContactAppUsers()
}