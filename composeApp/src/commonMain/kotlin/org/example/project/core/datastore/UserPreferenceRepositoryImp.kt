package org.example.project.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.example.project.core.datastore.model.UserData

class UserPreferencesRepositoryImpl(
    private val localDataSource: UserPreferencesDataSource
) : UserPreferencesRepository {

    override val userData: StateFlow<UserData>
        get() = localDataSource.userData

    override suspend fun updateUserData(userData: UserData) {
        localDataSource.updateUserData(userData)
    }

    override suspend fun saveToken(token : String){
        localDataSource.saveToken(token)
    }

    override suspend fun updateAddress(address: String) {
        localDataSource.updateAddress(address)
    }

    override suspend fun logOut() {
        localDataSource.clearUserData()
    }

    override  fun getUserData(): Flow<UserData> {
        return localDataSource.userData
    }

    // Add this implementation
    override suspend fun setLoggedIn(isLoggedIn: Boolean) {
        localDataSource.setLoggedIn(isLoggedIn)
    }
}