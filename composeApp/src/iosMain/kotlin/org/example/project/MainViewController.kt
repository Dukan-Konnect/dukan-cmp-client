package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.core.di.platformModule
import org.example.project.core.utils.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin(listOf(platformModule)) }
) { App() }