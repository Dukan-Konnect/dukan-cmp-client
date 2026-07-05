package org.example.project.core.notifications

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.example.project.core.data.repository.FcmRepository
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.utils.DataState
import org.koin.mp.KoinPlatform

class IosFirebaseNotificationBridge {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun syncRegistrationToken(token: String) {
        scope.launch {
            val preferencesRepository = getPreferencesRepository()
            preferencesRepository.updateFcmToken(token)

            val userData = preferencesRepository.userData.value
            if (!userData.isLoggedIn) return@launch
            if (userData.syncedFcmToken == token) return@launch

            when (getFcmRepository().syncFcmToken(token)) {
                is DataState.Success -> preferencesRepository.markFcmTokenSynced(token)
                is DataState.Error -> Unit
                DataState.Loading -> Unit
            }
        }
    }

    private fun getPreferencesRepository(): UserPreferencesRepository {
        return KoinPlatform.getKoin().get()
    }

    private fun getFcmRepository(): FcmRepository {
        return KoinPlatform.getKoin().get()
    }
}
