package org.example.project.core.utils

fun mapHttpStatusToUserMessage(statusCode: Int): String {
    return when (statusCode) {
        401, 403 -> SnackbarMessage.SESSION_ERROR
        404 -> SnackbarMessage.N0T_FOUND_ERROR
        409 -> SnackbarMessage.CONFLICT_ERROR
        in 500..599 -> SnackbarMessage.SERVER_ERROR
        else -> SnackbarMessage.GENERIC_ERROR
    }
}
