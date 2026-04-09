package org.example.project.core.utils

sealed class DataState<out T> {
    abstract val data: T?

    data object Loading : DataState<Nothing>() {
        override val data: Nothing? get() = null
    }

    data class Success<T>(
        override val data: T,
    ) : DataState<T>()

    data class Error<T>(
        val exception: Throwable,
        override val data: T? = null,
    ) : DataState<T>() {
        val message = exception.message.toString()
    }
}
