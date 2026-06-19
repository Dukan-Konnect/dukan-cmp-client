package org.example.project.core.data.repositoryImp

import org.example.project.core.data.mapper.toDomain
import org.example.project.core.data.repository.HomeRepository
import org.example.project.core.model.home.Service
import org.example.project.core.network.services.HomeService
import org.example.project.core.utils.ApiCallHelper
import org.example.project.core.utils.DataState

class HomeRepositoryImpl(
    private val homeService: HomeService,
    private val apiCallHelper: ApiCallHelper
) : HomeRepository {

    override suspend fun getPersonalServices(): DataState<List<Service>> = apiCallHelper.execute {
        homeService.getServicesByCategory("PERSONAL").toDomain()
    }

    override suspend fun getHomeServices(): DataState<List<Service>> = apiCallHelper.execute {
        homeService.getServicesByCategory("HOME").toDomain()
    }

    override suspend fun getTrendingServices(): DataState<List<Service>> = apiCallHelper.execute {
        homeService.getServicesByCategory("TRENDING").toDomain()
    }
}