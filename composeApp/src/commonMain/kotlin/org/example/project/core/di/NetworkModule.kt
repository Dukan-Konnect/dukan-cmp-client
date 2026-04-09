package org.example.project.core.di

import io.ktor.client.HttpClient
import org.example.project.core.datastore.UserPreferencesRepository
import org.example.project.core.network.KtorfitClient
import org.example.project.core.network.ktorHttpClient
import org.example.project.core.network.services.AuthenticationService
import org.example.project.core.utils.BaseURL
import org.example.project.core.utils.KtorInterceptor
import org.koin.dsl.module


val networkModule = module {

    single<HttpClient> {
        val preferencesRepository = get<UserPreferencesRepository>()

        ktorHttpClient.config {

            install(KtorInterceptor) {

                getToken = { preferencesRepository.token.value }

            }
        }
    }

    single<KtorfitClient> {
        KtorfitClient.builder()
            .httpClient(get<HttpClient>())
            .baseURL(BaseURL().url)
            .build()
    }

    single<AuthenticationService> {
        get<KtorfitClient>().authenticationApi
    }


}
