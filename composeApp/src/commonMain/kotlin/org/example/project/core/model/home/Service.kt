package org.example.project.core.model.home

import org.example.project.core.network.dto.home.ServiceCategory

data class Service(
    val id: Int,
    val name: String,
    val icon: String,
    val category: ServiceCategory
)

