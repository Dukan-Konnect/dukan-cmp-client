package org.example.project.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.example.project.core.utils.DataState
import org.example.project.core.utils.SnackbarMessage

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}

fun <T> NetworkMonitor.withNetworkCheck(
    upstream: Flow<DataState<T>>,
): Flow<DataState<T>> = combine(isOnline, upstream) { isOnline, dataState ->
    when {
        dataState is DataState.Success -> dataState
        dataState is DataState.Loading -> dataState
        !isOnline -> DataState.Error(Exception(SnackbarMessage.NETWORK_ERROR))
        else -> dataState
    }
}