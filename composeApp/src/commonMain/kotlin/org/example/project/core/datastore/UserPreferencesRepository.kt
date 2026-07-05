package org.example.project.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.core.datastore.model.UserData

interface UserPreferencesRepository {
    val userData: StateFlow<UserData>
    suspend fun updateUserData(userData: UserData)

    suspend fun updateName(name: String)
    suspend fun updateEmail(email: String)
    suspend fun updatePhoneNumber(phoneNumber: String)
    suspend fun updateAddress(address: String)
    suspend fun updateToken(token: String)
    suspend fun updateFcmToken(token: String)
    suspend fun markFcmTokenSynced(token: String)
    suspend fun setLoggedIn(isLoggedIn: Boolean)

    suspend fun logOut()

    suspend fun setOnboardingCompleted(completed: Boolean)

     fun getUserData(): Flow<UserData>


}
