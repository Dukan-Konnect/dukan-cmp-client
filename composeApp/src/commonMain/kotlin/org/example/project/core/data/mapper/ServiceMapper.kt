package org.example.project.core.data.mapper

import org.example.project.core.model.home.Service
import org.example.project.core.network.dto.home.ServiceDto


fun ServiceDto.toDomain(): Service {
    return Service(
        id = this.id,
        name = this.name ?: "Unknown Service",
        icon = this.icon ?: "",
        category = this.category
    )
}


fun List<ServiceDto>.toDomain(): List<Service> {
    return this.map { it.toDomain() }
}