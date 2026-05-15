package org.example.project.core.network.dto.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceDto(
    val id: Int,
    val name: String,
    val icon: String,
    @SerialName("category")
    val category: ServiceCategory
)
@Serializable
enum class ServiceCategory {
    @SerialName("PERSONAL")
    PERSONAL,
    @SerialName("HOME")
    HOME,
    @SerialName("TRENDING")
    TRENDING
}

