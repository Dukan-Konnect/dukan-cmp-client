package org.example.project.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val name: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val email: String = "",
    val token: String = "",
    val fcmToken: String = "",
    val syncedFcmToken: String = "",
    val hasSeenOnboarding: Boolean = false,
    val isLoggedIn: Boolean = false
) {
    companion object {
        val DEFAULT = UserData()
    }
}
