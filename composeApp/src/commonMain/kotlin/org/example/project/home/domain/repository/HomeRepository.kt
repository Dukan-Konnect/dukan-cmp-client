package org.example.project.home.domain.repository

import org.example.project.home.domain.model.Banner
import org.example.project.home.domain.model.Service
import org.example.project.home.domain.model.UserLocation

interface HomeRepository {
    suspend fun getPersonalServices(): Result<List<Service>>
    suspend fun getHomeServices(): Result<List<Service>>
    suspend fun getTrendingServices(): Result<List<Service>>
    suspend fun getBanner(): Result<Banner>
    suspend fun getUserLocation(): Result<UserLocation>
    suspend fun updateUserLocation(location: UserLocation): Result<Unit>
}

