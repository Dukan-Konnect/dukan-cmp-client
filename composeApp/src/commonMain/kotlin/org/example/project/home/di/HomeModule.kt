package org.example.project.home.di

import org.example.project.home.data.HomeRepositoryImpl
import org.example.project.home.data.ServiceDetailsRepositoryImpl
import org.example.project.home.domain.repository.HomeRepository
import org.example.project.home.domain.repository.ServiceDetailsRepository
import org.example.project.home.presentation.viewmodels.HomeViewModel
import org.example.project.home.presentation.viewmodels.ServiceDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {

    single<HomeRepository> { HomeRepositoryImpl(get()) } // Repository
    single<ServiceDetailsRepository> { ServiceDetailsRepositoryImpl(get()) } // Service Details Repository

    // ViewModels
    viewModel { HomeViewModel(get()) }
    viewModel { ServiceDetailsViewModel(get()) }
}
