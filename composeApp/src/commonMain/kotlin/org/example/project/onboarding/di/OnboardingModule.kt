package org.example.project.onboarding.di

import org.example.project.onboarding.presentation.viewmodel.AuthViewModel
import org.example.project.onboarding.presentation.viewmodel.LocationFetchViewModel
import org.example.project.onboarding.presentation.viewmodel.NameCaptureViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingModule = module {
    // Repository



    // ViewModels with dependencies
    viewModelOf(::AuthViewModel)
    viewModelOf(::LocationFetchViewModel)
    viewModelOf(::NameCaptureViewModel)

}
