package org.example.project.core.utils

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import org.example.project.core.log
import org.example.project.core.network.NetworkMonitor

class ApiCallHelper(
    private val networkMonitor: NetworkMonitor,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun <T> execute(apiCall: suspend () -> T): DataState<T> {
        return withContext(defaultDispatcher) {
            if (!networkMonitor.isOnline.first()) {
                log("SafeApi", "api call skipped due to network")
                return@withContext DataState.Error(Exception(SnackbarMessage.NETWORK_ERROR))
            }

            try {
                DataState.Success(apiCall())
            } catch (e: ClientRequestException) {
                DataState.Error(Exception(mapHttpStatusToUserMessage(e.response.status.value), e))
            } catch (e: ServerResponseException) {
                DataState.Error(Exception(SnackbarMessage.SERVER_ERROR, e))
            } catch (e: IOException) {
                DataState.Error(Exception(SnackbarMessage.NETWORK_ERROR, e))
            } catch (e: Exception) {
                DataState.Error(Exception(SnackbarMessage.GENERIC_ERROR, e))
            }
        }
    }
}
