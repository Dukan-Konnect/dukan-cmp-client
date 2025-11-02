package org.example.project.home.domain.repository

import io.github.jan.supabase.auth.providers.builtin.SSO
import org.example.project.home.domain.model.ServiceDetails

interface ServiceDetailsRepository {
    suspend fun getServiceDetails(serviceId: Int): SSO.Result<ServiceDetails>
}
