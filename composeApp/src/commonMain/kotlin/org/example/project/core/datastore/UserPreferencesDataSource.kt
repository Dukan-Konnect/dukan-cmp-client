package org.example.project.core.datastore

import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class UserPreferencesDataSource(
    private val settings: Settings
) {
    companion object {
        private const val TOKEN_KEY = "auth_token"
        private const val IS_LOGGED_IN_KEY = "is_logged_in"
    }

    private val _token = MutableStateFlow(settings.getString(TOKEN_KEY, ""))
    val token: StateFlow<String> = _token.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(settings.getBoolean(IS_LOGGED_IN_KEY, false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Wrapped in Dispatchers.IO for safe disk writing
    suspend fun saveToken(newToken: String) {
        withContext(Dispatchers.IO) {
            settings.putString(TOKEN_KEY, newToken)
            _token.value = newToken
        }
    }

    suspend fun clearToken() {
        withContext(Dispatchers.IO) {
            settings.remove(TOKEN_KEY)
            _token.value = ""
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        withContext(Dispatchers.IO) {
            settings.putBoolean(IS_LOGGED_IN_KEY, isLoggedIn)
            _isLoggedIn.value = isLoggedIn
        }
    }
}