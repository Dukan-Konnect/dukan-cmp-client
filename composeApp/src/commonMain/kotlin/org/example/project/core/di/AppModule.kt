package org.example.project.core.di

import org.example.project.core.data.di.RepositoryModule
import org.example.project.core.network.di.networkModule
import org.example.project.core.datastore.di.preferencesModule
import org.example.project.onboarding.di.onboardingModule
import org.example.project.home.di.homeModule
import org.example.project.home.di.cartModule
import org.example.project.payment.di.paymentModule
import org.koin.dsl.module

val appModules = module {
    includes(RepositoryModule)
    includes(preferencesModule)
    includes(networkModule)
    includes(onboardingModule)
    includes(supabaseModule)
    includes(cartModule)
    includes(homeModule)
    includes(paymentModule)
}
