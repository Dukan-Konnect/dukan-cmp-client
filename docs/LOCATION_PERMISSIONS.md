# Location Permission System

## Overview
The location permission handling has been separated from `LocationProvider` into a dedicated `LocationPermissionHandler` class for Android.

## Architecture

### Common Layer
- `LocationProvider` - Platform-agnostic interface for fetching location (expect/actual)
- `LocationFetchScreen` - UI for location fetching
- `LocationFetchScreenWithPermissions` - Platform-specific wrapper (expect/actual)

### Android Implementation

#### LocationProvider (android)
- **Purpose**: Fetch device location using Android LocationManager
- **Context**: Uses Application Context (singleton)
- **No permission handling** - assumes permissions are already granted

#### LocationPermissionHandler
- **Purpose**: Handle runtime location permission requests
- **Context**: Uses Application Context for permission checks
- **Methods**:
  - `hasLocationPermission()`: Check if permissions are granted
  - `requestLocationPermission(activity: Activity)`: Request permissions with dialog
  - `shouldShowRequestPermissionRationale(activity: Activity)`: Check if rationale needed

#### LocationFetchScreenWithPermissions (android)
- **Purpose**: Wrapper composable that requests permissions before showing LocationFetchScreen
- **Implementation**: Uses `LaunchedEffect` to request permissions when screen is composed

## Usage

### In Navigation (Already Configured)
```kotlin
composable<LocationFetchRoute> {
    LocationFetchScreenWithPermissions(
        onLocationFetched = { /* navigate */ }
    )
}
```

### Manual Usage in Activity (if needed)
```kotlin
class MyActivity : ComponentActivity() {
    private val locationProvider: LocationProvider by inject()
    private val permissionHandler: LocationPermissionHandler by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request permission first
        if (!permissionHandler.hasLocationPermission()) {
            permissionHandler.requestLocationPermission(this)
        } else {
            // Use location provider
            lifecycleScope.launch {
                val location = locationProvider.getCurrentLocation()
            }
        }
    }
    
    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationPermissionHandler.REQUEST_LOCATION_PERMISSIONS) {
            if (permissionHandler.hasLocationPermission()) {
                // Permission granted, fetch location
            } else {
                // Permission denied
            }
        }
    }
}
```

### In ViewModel (Always check permissions before calling)
```kotlin
class MyViewModel(
    private val locationProvider: LocationProvider
) : ViewModel() {
    
    suspend fun fetchLocation() {
        // Permissions should be requested by UI layer before calling this
        // LocationProvider will throw SecurityException if permissions not granted
        val location = locationProvider.getCurrentLocation()
    }
}
```

## Dependency Injection

Both components are provided as singletons in `coreAndroidModule`:

```kotlin
val coreAndroidModule = module {
    single { LocationProvider(androidContext()) }
    single { LocationPermissionHandler(androidContext()) }
}
```

## Permissions in AndroidManifest.xml

Ensure these permissions are declared:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Best Practices

1. **Always request permissions before fetching location**
2. **Use LocationPermissionHandler in Activity/Composable** (UI layer)
3. **Use LocationProvider in ViewModel/Repository** (domain layer)
4. **Don't pass Activity context to singletons** - use parametersOf() or inject at usage site
5. **Handle permission denial gracefully** - show error messages to user

## iOS Implementation (TODO)

The iOS implementation currently uses a stub. Future implementation should:
- Use CLLocationManager for location services
- Handle authorization status
- Request authorization before accessing location

