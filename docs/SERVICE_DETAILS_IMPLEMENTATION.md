# Service Details Feature Implementation

## Overview
Implemented a complete service details screen that fetches data from Supabase according to the three-table schema (services, categories, subservices).

## Database Schema
```sql
-- services table: bigserial id
-- categories table: uuid id, references services(id)
-- subservices table: uuid id, references categories(id)
```

## Architecture Layers

### 1. Domain Layer

#### Models (`ServiceDetails.kt`)
- **ServiceDetails**: Main service with Long id (bigint)
  - Contains service name, banner, rating info
  - Lists of categories and sections
  
- **CategoryItem**: Service category with String id (UUID)
  - Used for filtering subservices
  
- **SubService**: Individual service item with String id (UUID)
  - Price in whole currency units (converted from cents)
  - Duration in minutes
  - Rating and review count
  
- **ServiceSection**: Groups subservices by category
  - Each section corresponds to a category

#### Repository Interface (`ServiceDetailsRepository.kt`)
```kotlin
interface ServiceDetailsRepository {
    suspend fun getServiceDetails(serviceId: Long): Result<ServiceDetails>
}
```

### 2. Data Layer

#### Implementation (`ServiceDetailsRepositoryImpl.kt`)
- Injects `SupabaseClient` via Koin
- Fetches data in three queries:
  1. Get service by id from `services` table
  2. Get all categories for the service from `categories` table
  3. Get all subservices for those categories from `subservices` table
  
- Maps database DTOs to domain models:
  - Converts `price_cents` to whole currency units (divides by 100)
  - Groups subservices by category_id
  - Creates sections from categories with their subservices

#### Serializable DTOs
- `ServiceRow`: Maps to services table
- `CategoryRow`: Maps to categories table with `@SerialName` annotations
- `SubServiceRow`: Maps to subservices table with proper field mapping

### 3. Presentation Layer

#### ViewModel (`ServiceDetailsViewModel.kt`)
- **State Management**:
  - `isLoading`: Shows loading indicator
  - `serviceDetails`: Current service data
  - `selectedCategory`: Currently filtered category
  - `filteredSections`: Sections to display (all or filtered by category)
  - `errorMessage`: Error handling
  
- **Events**:
  - `LoadService(serviceId: Long)`: Fetch service details
  - `CategorySelected(categoryId: String)`: Filter by category UUID
  - `SubServiceClicked(subServiceId: String)`: Navigate to subservice
  - `BackClicked`: Navigate back
  - `Retry`: Retry failed request
  - `ErrorDismissed`: Clear error message
  
- **Effects**:
  - `NavigateBack`: Trigger back navigation
  - `NavigateToSubServiceDetails(subServiceId: String)`: Navigate to detail
  - `ShowMessage(message: String)`: Show snackbar

#### UI (`ServiceDetailScreen.kt`)
- **Service Header**: Banner image with title
- **Service Info Card**: Rating, bookings, custom package option
- **Categories Row**: Horizontal scrollable category chips
  - Clicking filters sections to show only that category's subservices
- **Service Sections**: Expandable sections by category
  - Each section shows its subservices
  - Subservices have image, title, rating, duration, price
  - Add/quantity controls for cart functionality

### 4. Dependency Injection

#### Supabase Module (`SupabaseModule.kt`)
- Provides singleton `SupabaseClient` with Postgrest installed

#### Home Module (`HomeModule.kt`)
```kotlin
single<ServiceDetailsRepository> { 
    ServiceDetailsRepositoryImpl(get()) 
}
viewModel { ServiceDetailsViewModel(get()) }
```

## Key Features

1. **Type Safety**: Uses proper types (Long for service, String UUIDs for categories/subservices)
2. **Error Handling**: Result type with retry capability
3. **Category Filtering**: Click category to filter sections
4. **Expandable Sections**: Categories shown as expandable cards
5. **Reactive UI**: StateFlow for state, SharedFlow for one-time effects
6. **Clean Architecture**: Clear separation of domain, data, presentation layers

## Usage

```kotlin
// In navigation
ServiceDetailScreen(
    serviceId = 1L,
    onBackClick = { navController.popBackStack() },
    onSubServiceClick = { subServiceId -> 
        navController.navigate("subservice/$subServiceId")
    }
)
```

## Database Population Example

To test, populate your Supabase tables:

```sql
-- Insert a service
INSERT INTO services (name, service_category, thumbnail) 
VALUES ('Salon Classic', 'PERSONAL', 'https://example.com/salon.jpg');

-- Insert categories
INSERT INTO categories (service_id, name, icon_url)
VALUES 
  (1, 'Hair Care', 'https://example.com/hair.png'),
  (1, 'Skin Care', 'https://example.com/skin.png');

-- Insert subservices
INSERT INTO subservices (category_id, title, price_cents, duration_min, rating, rating_count, thumbnail_url)
VALUES 
  ('category-uuid-1', 'Haircut', 50000, 45, 4.5, 120, 'https://example.com/haircut.jpg'),
  ('category-uuid-1', 'Hair Coloring', 150000, 90, 4.7, 85, 'https://example.com/color.jpg');
```

## Notes

- Prices are stored in cents/paise in DB, displayed in whole units (INR)
- Each section corresponds to one category with its subservices
- Category filtering updates `filteredSections` to show only selected category
- All IDs properly typed: Long for services, String UUID for categories/subservices

