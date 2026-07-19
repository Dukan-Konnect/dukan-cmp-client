package org.example.project.core.data.di

import org.example.project.core.data.repository.AuthRepository
import org.example.project.core.data.repository.FcmRepository
import org.example.project.profile.domain.repository.ProfileRepository
import org.example.project.core.data.repositoryImpl.AuthRepositoryImpl
import org.example.project.core.data.repositoryImpl.FcmRepositoryImpl
import org.example.project.profile.data.repository.ProfileRepositoryImpl
import org.example.project.home.data.repository.HomeRepositoryImpl
import org.example.project.home.data.repository.ServiceDetailsRepositoryImpl
import org.example.project.home.domain.repository.HomeRepository
import org.example.project.home.domain.repository.ServiceDetailsRepository
import org.koin.dsl.module

val RepositoryModule = module {

    single<AuthRepository>{ AuthRepositoryImpl(get(),get(), get()) }
    single<FcmRepository>{ FcmRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }

    single<HomeRepository> { HomeRepositoryImpl(get(), get()) }
    single<ServiceDetailsRepository> { ServiceDetailsRepositoryImpl(get(), get()) }
} 
