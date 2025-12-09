package org.example.project.home.domain.repository

import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceProvider

interface ServiceDetailsRepository {
    suspend fun getServiceDetails(serviceId: Long): Result<ServiceDetails>
    suspend fun getServiceProviders(subserviceId: String): Result<List<ServiceProvider>>
}
