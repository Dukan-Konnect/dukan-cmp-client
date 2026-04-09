package org.example.project.core.datastore

import kotlinx.coroutines.flow.StateFlow

interface UserPreferencesRepository {
    val token: StateFlow<String>

    // Add the new flow here
    val isLoggedIn: StateFlow<Boolean>

    suspend fun saveToken(token: String)

    // Add the setter here
    suspend fun setLoggedIn(isLoggedIn: Boolean)

    suspend fun logOut()

}