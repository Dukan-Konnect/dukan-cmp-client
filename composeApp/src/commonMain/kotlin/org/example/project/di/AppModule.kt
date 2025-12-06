package org.example.project.di

import org.example.project.core.di.coreModule
import org.example.project.core.di.supabaseModule
import org.example.project.onboarding.di.onboardingModule
import org.example.project.home.di.homeModule
import org.example.project.home.di.cartModule
import org.example.project.payment.di.paymentModule
import org.koin.dsl.module

val appModules = module {
    includes(coreModule)
    includes(onboardingModule)
    includes(supabaseModule)
    includes(cartModule)
    includes(homeModule)
    includes(paymentModule)
}
