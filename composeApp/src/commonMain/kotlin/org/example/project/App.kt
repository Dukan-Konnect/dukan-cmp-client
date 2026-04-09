package org.example.project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import coil3.compose.setSingletonImageLoaderFactory
import org.example.project.core.datastore.UserPreferencesDataSource
import org.example.project.core.navigation.AppNavGraph
import org.example.project.core.navigation.HomeRoute
import org.example.project.core.navigation.OnboardingRoute
import org.example.project.core.utils.createImageLoader
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    setSingletonImageLoaderFactory { context ->
        createImageLoader(context)
    }
    MaterialTheme {
        val userPreferences: UserPreferencesDataSource = koinInject()

        val isLoggedIn by userPreferences.isLoggedIn.collectAsState()

        val start = if (isLoggedIn) HomeRoute else OnboardingRoute

        Box(modifier = Modifier.fillMaxSize()) {
            AppNavGraph(startDestination = start)
        }
    }
}
