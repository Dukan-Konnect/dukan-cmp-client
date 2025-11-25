package org.example.project.home.di

import org.example.project.home.data.repository.HomeRepositoryImpl
import org.example.project.home.data.repository.ServiceDetailsRepositoryImpl
import org.example.project.home.domain.repository.HomeRepository
import org.example.project.home.domain.repository.ServiceDetailsRepository
import org.example.project.home.presentation.viewmodels.HomeViewModel
import org.example.project.home.presentation.viewmodels.ServiceDetailsViewModel
import org.example.project.home.presentation.viewmodels.SummaryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {

    single<HomeRepository> { HomeRepositoryImpl(get()) } // Repository
    single<ServiceDetailsRepository> { ServiceDetailsRepositoryImpl(get()) } // Service Details Repository

    // ViewModels
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ServiceDetailsViewModel(get(),get()) }
    viewModel { SummaryViewModel(get(), get()) }
}
