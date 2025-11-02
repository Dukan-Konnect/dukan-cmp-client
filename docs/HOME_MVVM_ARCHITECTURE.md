# HomeScreen MVVM Architecture

## Overview
This document explains the MVVM (Model-View-ViewModel) architecture implemented for the HomeScreen.

## Architecture Layers

### 1. Domain Layer (`domain/`)
Contains business logic and data models:

#### Models (`domain/model/`)
- **Service.kt**: Represents a service item with id, name, icon, and category
- **Banner.kt**: Represents promotional banner data
- **UserLocation.kt**: Represents user's location information

#### Repository Interface (`domain/repository/`)
- **HomeRepository.kt**: Defines contract for data operations
  - `getPersonalServices()`: Fetch personal services
  - `getHomeServices()`: Fetch home services
  - `getTrendingServices()`: Fetch trending services
  - `getBanner()`: Fetch banner data
  - `getUserLocation()`: Get user's current location
  - `updateUserLocation()`: Update user's location

### 2. Data Layer (`data/`)
Contains data implementation:

- **HomeRepositoryImpl.kt**: Implements HomeRepository interface
  - Currently uses mock data
  - TODO: Replace with actual API calls using Ktor/Supabase
  - Returns `Result<T>` for proper error handling

### 3. Presentation Layer (`presentation/`)

#### State (`presentation/state/`)
- **HomeUiState.kt**: Single source of truth for UI state
  ```kotlin
  data class HomeUiState(
      val isLoading: Boolean = false,
      val userLocation: UserLocation? = null,
      val banner: Banner? = null,
      val personalServices: List<Service> = emptyList(),
      val homeServices: List<Service> = emptyList(),
      val trendingServices: List<Service> = emptyList(),
      val errorMessage: String? = null
  )
  ```

#### ViewModel (`presentation/viewmodels/`)
- **HomeViewModel.kt**: MVVM ViewModel
  - Extends `androidx.lifecycle.ViewModel`
  - Uses `StateFlow` for reactive state management
  - Handles business logic and user interactions
  - Methods:
    - `loadHomeData()`: Load all home screen data
    - `onLocationClick()`: Handle location selection
    - `onSearchClick()`: Handle search action
    - `onServiceClick(serviceId)`: Handle service selection
    - `onBannerClick()`: Handle banner click
    - `updateLocation(location)`: Update user location
    - `retry()`: Retry loading data on error
    - `clearError()`: Clear error message

#### Screens (`presentation/screens/`)
- **HomeScreen.kt**: Main composable
  - Uses `koinViewModel()` to inject HomeViewModel
  - Collects state using `collectAsState()`
  - Delegates all logic to ViewModel
  - Pure UI rendering based on state

### 4. Dependency Injection (`di/`)
- **HomeModule.kt**: Koin module for home feature
  - Provides HomeRepository singleton
  - Provides HomeViewModel with Koin's viewModel DSL

## MVVM Pattern Benefits

1. **Separation of Concerns**: UI logic separated from business logic
2. **Testability**: ViewModel can be unit tested without UI
3. **Reactive State**: StateFlow ensures UI updates automatically
4. **Lifecycle Awareness**: ViewModel survives configuration changes
5. **Single Source of Truth**: All UI state in one place (HomeUiState)

## Data Flow

```
User Action → View (HomeScreen)
              ↓
              ViewModel (HomeViewModel)
              ↓
              Repository (HomeRepository)
              ↓
              Data Source (API/Local)
              ↓
              Repository returns Result<T>
              ↓
              ViewModel updates StateFlow
              ↓
              View observes StateFlow and re-renders
```

## Key MVVM Principles Used

1. **Unidirectional Data Flow**: State flows down, events flow up
2. **Immutable State**: HomeUiState is immutable data class
3. **State Hoisting**: ViewModel owns state, View is stateless
4. **Reactive Updates**: StateFlow automatically notifies observers
5. **Error Handling**: Errors captured in state, not thrown to UI

## How to Extend

### Add New Data:
1. Add property to HomeUiState
2. Update loadHomeData() in ViewModel
3. Update UI to display new data

### Add New Action:
1. Add method to ViewModel
2. Call it from HomeScreen composable
3. Update state in ViewModel method

### Connect to Real API:
1. Update HomeRepositoryImpl
2. Replace mock data with actual API calls
3. Use Ktor client or Supabase SDK

## Testing Strategy

### ViewModel Tests:
- Test state updates
- Test error handling
- Test user actions
- Mock repository responses

### Repository Tests:
- Test API integration
- Test error scenarios
- Test data mapping

### UI Tests:
- Test rendering with different states
- Test user interactions
- Use preview with mock ViewModel

## Usage Example

```kotlin
@Composable
fun HomeScreen(
    onServiceClick: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // UI renders based on uiState
    if (uiState.isLoading) {
        LoadingIndicator()
    }
    
    // User action triggers ViewModel method
    Button(onClick = { viewModel.onBannerClick() }) {
        Text("Click Banner")
    }
}
```

## Next Steps

1. ✅ Created domain models
2. ✅ Created repository interface and implementation
3. ✅ Created UI state
4. ✅ Created ViewModel with StateFlow
5. ✅ Integrated with HomeScreen
6. ✅ Set up Koin DI
7. 🔄 Replace mock data with real API calls
8. 🔄 Add unit tests for ViewModel
9. 🔄 Add error handling improvements
10. 🔄 Add loading states for individual sections

