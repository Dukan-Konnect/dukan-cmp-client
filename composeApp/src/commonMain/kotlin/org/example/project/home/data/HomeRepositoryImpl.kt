package org.example.project.home.data

import org.example.project.home.domain.model.Banner
import org.example.project.home.domain.model.Service
import org.example.project.home.domain.model.ServiceCategory
import org.example.project.home.domain.model.UserLocation
import org.example.project.home.domain.repository.HomeRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import org.example.project.core.log


class HomeRepositoryImpl(
    private val supabase: SupabaseClient
) : HomeRepository {

    override suspend fun getPersonalServices(): Result<List<Service>> {
        return try {
            val serviceList = supabase.from("services")
                .select() {
                    filter {
                        eq("service_category", "PERSONAL")
                    }
                }
                .decodeList<Service>()

            val services = serviceList.map { dto ->
                Service(
                    id = dto.id,
                    name = dto.name,
                    icon = dto.icon,
                    category = ServiceCategory.PERSONAL
                )
            }
            log("homeviewmodel","Fetched personal Services: $serviceList")
            Result.success(services)
        } catch (e: Exception) {
            log("homeviewmodel","Fetched: $e")
            Result.failure(e)
        }
    }

    override suspend fun getHomeServices(): Result<List<Service>> {
        return try {
            val serviceList = supabase.from("services")
                .select() {
                    filter {
                        eq("service_category", "HOME")
                    }
                }
                .decodeList<Service>()

            val services = serviceList.map { dto ->
                Service(
                    id = dto.id,
                    name = dto.name,
                    icon = dto.icon,
                    category = ServiceCategory.HOME
                )
            }
            log("homeviewmodel","Fetched home Services: $serviceList")
            Result.success(services)
        } catch (e: Exception) {
            log("homeviewmodel","Fetched: $e")
            Result.failure(e)
        }
    }

    override suspend fun getTrendingServices(): Result<List<Service>> {
        return try {
            val serviceList = supabase.from("services")
                .select() {
                    filter {
                        eq("service_category", "TRENDING")
                    }
                }
                .decodeList<Service>()

            val services = serviceList.map { dto ->
                Service(
                    id = dto.id,
                    name = dto.name,
                    icon = dto.icon,
                    category = ServiceCategory.TRENDING
                )
            }
            log("homeviewmodel","Fetched tren Services: $serviceList")
            Result.success(services)
        } catch (e: Exception) {
            log("homeviewmodel","Fetched: $e")
            Result.failure(e)
        }
    }

    override suspend fun getBanner(): Result<Banner> {
        return try {
            // TODO: Replace with actual API call
            val banner = Banner(
                id = "1",
                title = "Let's make a package just\nfor you, Kartikey",
                description = "Create your custom package",
                imageUrl = "drawable/banner_woman.png",
                actionText = "Make your own package"
            )
            Result.success(banner)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserLocation(): Result<UserLocation> {
        return try {
            // TODO: Replace with actual location service
            val location = UserLocation(
                address = "Kesnand Rd, opp. to Ayurvedic colla...",
                latitude = 0.0,
                longitude = 0.0
            )
            Result.success(location)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserLocation(location: UserLocation): Result<Unit> {
        return try {
            // TODO: Replace with actual API call to update location
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
