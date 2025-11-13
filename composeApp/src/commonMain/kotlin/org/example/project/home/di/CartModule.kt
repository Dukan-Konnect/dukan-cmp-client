package org.example.project.home.di

import org.koin.dsl.module
import org.example.project.home.data.local.database.CartDatabase
import org.example.project.home.data.repository.CartRepositoryImpl
import org.example.project.home.domain.repository.CartRepository
import org.example.project.home.domain.usecase.CartUseCases

expect val cartPlatformModule: org.koin.core.module.Module

val cartModule = module {
    includes(cartPlatformModule)

    // DAO
    single { get<CartDatabase>().cartDao() }

    // Repository
    single<CartRepository> { CartRepositoryImpl(get()) }

    // Use Cases
    single { CartUseCases(get()) }
}

