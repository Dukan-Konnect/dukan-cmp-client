package org.example.project.core.utils

import org.example.project.core.datastore.UserPreferencesRepository

suspend fun handleLogout(prefRepository: UserPreferencesRepository) {
    prefRepository.logOut()
}