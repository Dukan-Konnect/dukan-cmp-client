package org.example.project.home.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Service(
    val id: Int,
    val name: String,
    val icon: String,
    @SerialName("service_category")
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

