package org.example.project

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.example.project.core.utils.location.LocationProvider
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf



class MainActivity : ComponentActivity() {
    private val locationProvider: LocationProvider by inject { parametersOf(this) }
    /**
     * Called when the activity is first created.
     *
     * This method is responsible for initializing the activity. It sets up the splash screen,
     * enables edge-to-edge display for a modern UI, and sets the main content view of the activity
     * to the `App` composable. It also keeps the splash screen visible until the initial data is loaded.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [onSaveInstanceState].  **Note: Otherwise it is null.**
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)



      setContent{
          App()
      }
//        val intent = Intent(this, PaymentActivity::class.java)
//        startActivity(intent)
        // Optional: Keep splash screen visible while loading
        splashScreen.setKeepOnScreenCondition {
            // Return true to keep splash visible
            // Return false to dismiss splash
            false
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}