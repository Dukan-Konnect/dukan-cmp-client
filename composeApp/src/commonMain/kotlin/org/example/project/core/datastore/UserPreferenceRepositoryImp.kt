package org.example.project.core.datastore

import kotlinx.coroutines.flow.StateFlow

class UserPreferencesRepositoryImpl(
    private val localDataSource: UserPreferencesDataSource
) : UserPreferencesRepository {

    override val token: StateFlow<String>
        get() = localDataSource.token

    override val isLoggedIn: StateFlow<Boolean>
        get() = localDataSource.isLoggedIn

    override suspend fun saveToken(token: String) {
        localDataSource.saveToken(token)
    }

    override suspend fun setLoggedIn(isLoggedIn: Boolean) {
        localDataSource.setLoggedIn(isLoggedIn)
    }

    override suspend fun logOut() {
        localDataSource.clearToken()
        localDataSource.setLoggedIn(false)
    }
}