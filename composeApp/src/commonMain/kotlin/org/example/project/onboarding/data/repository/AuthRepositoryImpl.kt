package org.example.project.onboarding.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.providers.builtin.OTP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.example.project.core.log
import org.example.project.onboarding.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val supabase: SupabaseClient
) : AuthRepository {

    override suspend fun sendOtp(phoneNumber: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            supabase.auth.signInWith(OTP) {
                phone = phoneNumber
            }
            return@withContext Result.success(true)

        } catch (t: Throwable) {
            return@withContext Result.failure(t)
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<String> = withContext(Dispatchers.IO) {
        log("authviewmodel", "verify: $phoneNumber")
        try {
            supabase.auth.verifyPhoneOtp(type = OtpType.Phone.SMS, phone = phoneNumber, token = otp)
            return@withContext Result.success("Verified Successfully")

        } catch (t: Throwable) {
            return@withContext Result.failure(t)
        }
    }

    override suspend fun createAccount(phoneNumber: String): Result<Boolean> = withContext(Dispatchers.IO) {
        return@withContext Result.success(true)
    }
//        try {
//            supabase.auth.signInWith(Email) {
//                email = "kartikeyshukla15@gmail.com"
//                password = "example-password"
//            }
//            return@withContext Result.success(true)
//
//        } catch (t: Throwable) {
//            return@withContext Result.failure(t)
//        }
//    }
}
