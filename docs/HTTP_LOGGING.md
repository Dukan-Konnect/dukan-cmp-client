# HttpClient Logging Configuration

## Current Status

The HttpClient in `CoreModule.kt` is configured **without logging** to avoid import errors before Gradle sync.

## To Enable Logging (Optional)

After Gradle syncs the new dependencies, you can enable HTTP request/response logging:

### Step 1: Verify Gradle Sync
1. Open the project in Android Studio
2. Click "Sync Now" or run: `./gradlew build`
3. Wait for sync to complete

### Step 2: Enable Logging in CoreModule.kt

Replace the HttpClient configuration with:

```kotlin
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*  // ← Add this import
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val coreModule = module {
    single { AuthSettings() }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // ← Add logging plugin
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO  // Options: ALL, HEADERS, BODY, INFO, NONE
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }

            defaultRequest {
                url("https://api.razorpay.com/v1/")
            }
        }
    }
}
```

### What Logging Shows

With logging enabled, you'll see in Logcat:
- **Request URL**: `POST https://api.razorpay.com/v1/orders`
- **Request Headers**: Authorization, Content-Type, etc.
- **Request Body**: Order details (amount, currency, receipt)
- **Response Status**: 200 OK or error codes
- **Response Body**: Order ID and other details

### Log Levels

- `LogLevel.ALL` - Everything (headers + body)
- `LogLevel.HEADERS` - Request/response headers only
- `LogLevel.BODY` - Request/response body only
- `LogLevel.INFO` - Basic request info
- `LogLevel.NONE` - Disable logging

## Why Logging is Optional

- ✅ **Not required** for functionality
- ✅ Helpful for debugging API calls
- ✅ Can see request/response details
- ⚠️ May expose sensitive data in logs (API keys, tokens)
- ⚠️ Adds overhead to requests

## For Production

**Disable or set to `LogLevel.NONE`** in production builds to:
- Improve performance
- Reduce log file size
- Prevent sensitive data exposure

## Dependencies Added

Already added to `libs.versions.toml` and `build.gradle.kts`:
```toml
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktorVersion" }
```

Just waiting for Gradle sync to download it.

