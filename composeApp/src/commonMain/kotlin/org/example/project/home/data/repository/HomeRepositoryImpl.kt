package org.example.project.home.data.repository

import org.example.project.core.data.mapper.toDomain
import org.example.project.core.model.home.Service
import org.example.project.core.network.services.HomeService
import org.example.project.core.utils.ApiCallHelper
import org.example.project.core.utils.DataState
import org.example.project.home.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val homeService: HomeService,
    private val apiCallHelper: ApiCallHelper
) : HomeRepository {

    private var personalCache: List<Service>? = null
    private var homeCache: List<Service>? = null
    private var trendingCache: List<Service>? = null

    override val hasCache: Boolean
        get() = personalCache != null && homeCache != null && trendingCache != null

    override fun getCachedPersonalServices(): List<Service> = personalCache ?: emptyList()
    override fun getCachedHomeServices(): List<Service> = homeCache ?: emptyList()
    override fun getCachedTrendingServices(): List<Service> = trendingCache ?: emptyList()

    override suspend fun getPersonalServices(): DataState<List<Service>> {
        val result = apiCallHelper.execute {
            homeService.getServicesByCategory("PERSONAL").toDomain()
        }
        if (result is DataState.Success) {
            personalCache = result.data
        }
        return result
    }

    override suspend fun getHomeServices(): DataState<List<Service>> {
        val result = apiCallHelper.execute {
            homeService.getServicesByCategory("HOME").toDomain()
        }
        if (result is DataState.Success) {
            homeCache = result.data
        }
        return result
    }

    override suspend fun getTrendingServices(): DataState<List<Service>> {
        val result = apiCallHelper.execute {
            homeService.getServicesByCategory("TRENDING").toDomain()
        }
        if (result is DataState.Success) {
            trendingCache = result.data
        }
        return result
    }
}