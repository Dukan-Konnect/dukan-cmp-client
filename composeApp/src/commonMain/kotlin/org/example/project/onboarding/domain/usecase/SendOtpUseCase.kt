
import org.example.project.onboarding.domain.repository.AuthRepository

class SendOtpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String): Result<Boolean> {
        // Validate phone number
//        if (phoneNumber.length != 10) {
//            return Result.failure(Exception("Invalid phone number"))
//        }

        return authRepository.sendOtp(phoneNumber)
    }
}


