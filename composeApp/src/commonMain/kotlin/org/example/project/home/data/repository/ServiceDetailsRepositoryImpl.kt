package org.example.project.home.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.project.core.log
import org.example.project.home.domain.model.CategoryItem
import org.example.project.home.domain.model.ServiceDetails
import org.example.project.home.domain.model.ServiceSection
import org.example.project.home.domain.model.SubService
import org.example.project.home.domain.repository.ServiceDetailsRepository

class ServiceDetailsRepositoryImpl(
    private val supabase: SupabaseClient
) : ServiceDetailsRepository {

    override suspend fun getServiceDetails(serviceId: Long): Result<ServiceDetails> = runCatching {
        val service = supabase.from("services")
            .select {
                filter {
                    eq("id", serviceId)
                }
            }
            .decodeSingle<ServiceRow>()

        val categories = supabase.from("categories")
            .select {
                filter {
                    eq("service_id", serviceId)
                }
            }
            .decodeList<CategoryRow>()
        log("servicedetails", "cats$categories")

        val categoryIds = categories.map { it.id }
        val subservices = if (categoryIds.isNotEmpty()) {
            supabase.from("subservices")
                .select {
                    filter {
                        "category_id" to "in.(${categoryIds.joinToString(",")})"
                    }
                }
                .decodeList<SubServiceRow>()
        } else emptyList()

        val categoryModels = categories.map {
            CategoryItem(
                id = it.id,
                label = it.name,
                image = it.iconUrl.orEmpty()
            )
        }

        val subservicesByCategory = subservices.groupBy { it.categoryId }

        val sections = categories.map { cat ->
            ServiceSection(
                id = cat.id,
                title = cat.name,
                items = (subservicesByCategory[cat.id] ?: emptyList()).map { s ->
                    SubService(
                        id = s.id,
                        title = s.title,
                        rating = s.rating ?: 0.0,
                        reviewCount = s.ratingCount ?: 0,
                        durationMin = s.durationMin,
                        price = s.priceCents / 100, // convert to whole currency
                        currency = s.currency ?: "INR",
                        image = s.thumbnailUrl.orEmpty()
                    )
                }
            )
        }

        ServiceDetails(
            id = service.id,
            title = service.name,
            bannerTitle = service.serviceCategory ?: service.name,
            bannerImage = service.thumbnail.orEmpty(),
            rating = 0.0, // no rating on service table; aggregate could be added later
            reviewCount = 0,
            bookingsText = "Same day bookings available",
            categories = categoryModels,
            sections = sections
        )
    }
}

@Serializable
private data class ServiceRow(
    val id: Long,
    val name: String,
    val icon: String? = null,
    @SerialName("service_category") val serviceCategory: String? = null,
    val thumbnail: String? = null
)

@Serializable
private data class CategoryRow(
    val id: String, // uuid
    @SerialName("service_id") val serviceId: Long,
    val name: String,
    @SerialName("icon_url") val iconUrl: String? = null
)

@Serializable
private data class SubServiceRow(
    val id: String, // uuid
    @SerialName("category_id") val categoryId: String,
    val title: String,
    @SerialName("price_cents") val priceCents: Int,
    val currency: String? = null,
    @SerialName("duration_min") val durationMin: Int? = null,
    val rating: Double? = null,
    @SerialName("rating_count") val ratingCount: Int? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null
)
