package org.example.project.core.datastore

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.decodeValueOrNull
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import org.example.project.core.datastore.model.UserData

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
class UserPreferencesDataSource(
    private val settings: Settings
) {
    companion object {
        private const val USER_DATA_KEY = "user_data"
    }

    private val _userData = MutableStateFlow(
        settings.decodeValue(
            key = USER_DATA_KEY,
            serializer = UserData.serializer(),
            defaultValue = settings.decodeValueOrNull(
                key = USER_DATA_KEY,
                serializer = UserData.serializer()
            ) ?: UserData.DEFAULT
        )
    )
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    suspend fun updateUserData(newUserData: UserData) {
        withContext(Dispatchers.IO) {
            settings.encodeValue(
                key = USER_DATA_KEY,
                serializer = UserData.serializer(),
                value = newUserData
            )
            _userData.value = newUserData
        }
    }

    suspend fun clearUserData() {
        withContext(Dispatchers.IO) {
            val defaultData = UserData.DEFAULT
            settings.encodeValue(
                key = USER_DATA_KEY,
                serializer = UserData.serializer(),
                value = defaultData
            )
            _userData.value = defaultData
        }
    }

    suspend fun saveToken(token: String) {
        // 1. Get the current user data
        val currentData = userData.value

        // 2. Create a copy with ONLY the isLoggedIn value changed
        val updatedData = currentData.copy(token = token)

        // 3. Save the new object
        updateUserData(updatedData)
    }

    suspend fun updateAddress(address: String) {
        // 1. Get the current user data
        val currentData = userData.value

        // 2. Create a copy with ONLY the isLoggedIn value changed
        val updatedData = currentData.copy(address = address)

        // 3. Save the new object
        updateUserData(updatedData)
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        // 1. Get the current user data
        val currentData = userData.value

        // 2. Create a copy with ONLY the isLoggedIn value changed
        val updatedData = currentData.copy(isLoggedIn = isLoggedIn)

        // 3. Save the new object
        updateUserData(updatedData)
    }
}