package org.example.project.di

import org.example.project.core.di.networkModule
import org.example.project.core.di.preferencesModule
import org.example.project.core.di.supabaseModule
import org.example.project.onboarding.di.onboardingModule
import org.example.project.home.di.homeModule
import org.example.project.home.di.cartModule
import org.example.project.payment.di.paymentModule
import org.koin.dsl.module

val appModules = module {
    includes(preferencesModule)
    includes(networkModule)
    includes(onboardingModule)
    includes(supabaseModule)
    includes(cartModule)
    includes(homeModule)
    includes(paymentModule)
}
