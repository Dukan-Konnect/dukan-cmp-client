package org.example.project.home.di

import org.example.project.home.presentation.viewmodels.HomeViewModel
import org.example.project.home.presentation.viewmodels.ServiceDetailsViewModel
import org.example.project.home.presentation.viewmodels.SummaryViewModel
import org.example.project.profile.presentation.viewmodels.EditAddressViewModel
import org.example.project.profile.presentation.viewmodels.ManageAddressViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    // ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::ServiceDetailsViewModel)
    viewModelOf(::SummaryViewModel)
    viewModelOf(::ManageAddressViewModel)
    viewModelOf(::EditAddressViewModel)
}
