package org.example.project.core.network.services

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import org.example.project.core.network.dto.home.ServiceDto
import org.example.project.core.utils.ApiEndPoints

interface HomeService {


    @GET(ApiEndPoints.SERVICES)
    suspend fun getServicesByCategory(
        @Query("category") category: String
    ): List<ServiceDto>

}













