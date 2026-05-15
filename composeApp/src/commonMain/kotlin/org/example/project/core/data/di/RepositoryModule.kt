package org.example.project.core.data.di

import org.example.project.core.data.repository.AuthRepository
import org.example.project.core.data.repository.ProfileRepository
import org.example.project.core.data.repositoryImp.AuthRepositoryImpl
import org.example.project.core.data.repositoryImp.ProfileRepositoryImpl
import org.example.project.core.data.repositoryImp.HomeRepositoryImpl
import org.example.project.home.data.repository.ServiceDetailsRepositoryImpl
import org.example.project.core.data.repository.HomeRepository
import org.example.project.home.domain.repository.ServiceDetailsRepository
import org.koin.dsl.module

val RepositoryModule = module {

    single<AuthRepository>{ AuthRepositoryImpl(get(),get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }

    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<ServiceDetailsRepository> { ServiceDetailsRepositoryImpl(get()) }
}