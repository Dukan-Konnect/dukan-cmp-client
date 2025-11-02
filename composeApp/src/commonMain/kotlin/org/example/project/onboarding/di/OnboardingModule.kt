package org.example.project.onboarding.di

import SendOtpUseCase
import org.example.project.onboarding.data.repository.AuthRepositoryImpl
import org.example.project.onboarding.domain.repository.AuthRepository
import org.example.project.onboarding.domain.usecase.VerifyOtpUseCase
import org.example.project.onboarding.presentation.viewmodel.AuthViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val onboardingModule = module {
    // Repository
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    // Use Cases
    factoryOf(::SendOtpUseCase)
    factoryOf(::VerifyOtpUseCase)

    // ViewModels with dependencies
    viewModelOf(::AuthViewModel)

}
