package org.example.project.home.data.repository

import org.example.project.core.network.services.ServiceDetailsService
import org.example.project.core.utils.ApiCallHelper
import org.example.project.core.utils.DataState
import org.example.project.booking.data.mapper.toDomain
import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceProvider
import org.example.project.home.domain.repository.ServiceDetailsRepository

class ServiceDetailsRepositoryImpl(
    private val serviceDetailsService: ServiceDetailsService,
    private val apiCallHelper: ApiCallHelper
) : ServiceDetailsRepository {

    override suspend fun getServiceDetails(serviceId: Long): Result<ServiceDetails> {
        return when (val state = apiCallHelper.execute { serviceDetailsService.getServiceDetails(serviceId).toDomain() }) {
            is DataState.Success -> Result.success(state.data)
            is DataState.Error -> Result.failure(state.exception)
            DataState.Loading -> Result.failure(IllegalStateException("Unexpected loading state"))
        }
    }

    override suspend fun getServiceProviders(subserviceId: String): Result<List<ServiceProvider>> {
        return when (val state = apiCallHelper.execute { serviceDetailsService.getProviders(subserviceId).map { it.toDomain() } }) {
            is DataState.Success -> Result.success(state.data)
            is DataState.Error -> Result.failure(state.exception)
            DataState.Loading -> Result.failure(IllegalStateException("Unexpected loading state"))
        }
    }
}

