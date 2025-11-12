package org.example.project.home.domain.repository

import org.example.project.home.domain.model.ServiceDetails

interface ServiceDetailsRepository {
    suspend fun getServiceDetails(serviceId: Long): Result<ServiceDetails>
}
