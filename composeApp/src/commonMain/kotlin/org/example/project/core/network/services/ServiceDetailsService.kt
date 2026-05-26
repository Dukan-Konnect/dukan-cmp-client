package org.example.project.core.network.services

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import org.example.project.core.network.dto.servicedetails.ServiceDetailsResponseDto
import org.example.project.core.network.dto.servicedetails.ServiceProviderDto

interface ServiceDetailsService {

    @GET("services/{serviceId}/details")
    suspend fun getServiceDetails(
        @Path("serviceId") serviceId: Long
    ): ServiceDetailsResponseDto

    @GET("subservices/{subserviceId}/providers")
    suspend fun getProviders(
        @Path("subserviceId") subserviceId: String
    ): List<ServiceProviderDto>
}

