package org.example.project.core.data.repositoryImp

import org.example.project.core.data.mapper.toDomain
import org.example.project.core.data.repository.HomeRepository
import org.example.project.core.model.home.Service
import org.example.project.core.network.services.HomeService
import org.example.project.core.utils.DataState
import org.example.project.core.utils.safeApiCall

class HomeRepositoryImpl(
    private val homeService: HomeService
) : HomeRepository {

    override suspend fun getPersonalServices(): DataState<List<Service>> = safeApiCall {
        homeService.getServicesByCategory("PERSONAL").toDomain()
    }

    override suspend fun getHomeServices(): DataState<List<Service>> = safeApiCall {
        homeService.getServicesByCategory("HOME").toDomain()
    }

    override suspend fun getTrendingServices(): DataState<List<Service>> = safeApiCall {
        homeService.getServicesByCategory("TRENDING").toDomain()
    }

}