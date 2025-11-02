package org.example.project.core

actual class AppwriteClient actual constructor() {
    actual suspend fun sendOtp(phone: String): Result<Boolean> {
        // iOS implementation not available yet; return failure to indicate unsupported
        return Result.failure(Exception("sendOtp not implemented on iOS"))
    }

    actual suspend fun verifyOtp(phone: String, otp: String): Result<String> {
        return Result.failure(Exception("verifyOtp not implemented on iOS"))
    }

    actual suspend fun createAccount(phone: String): Result<Boolean> {
        return Result.failure(Exception("createAccount not implemented on iOS"))
    }
}

actual fun log(tag: String, message: String) {
    Log.d(tag, message)
}