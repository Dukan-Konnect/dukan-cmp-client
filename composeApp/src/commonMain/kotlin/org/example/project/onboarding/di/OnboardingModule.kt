package org.example.project.onboarding.di

import org.example.project.onboarding.data.repository.AuthRepositoryImpl
import org.example.project.onboarding.domain.repository.AuthRepository
import org.example.project.onboarding.presentation.viewmodel.AuthViewModel
import org.example.project.onboarding.presentation.viewmodel.LocationFetchViewModel
import org.example.project.onboarding.presentation.viewmodel.NameCaptureViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val onboardingModule = module {
    // Repository
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class


    // ViewModels with dependencies
    viewModelOf(::AuthViewModel)
    viewModelOf(::LocationFetchViewModel)
    viewModelOf(::NameCaptureViewModel)

}
