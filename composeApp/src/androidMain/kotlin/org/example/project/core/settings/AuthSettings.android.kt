package org.example.project.core.settings

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

// Android actual implementation backed by Multiplatform Settings (no-arg)
actual class AuthSettings actual constructor() {
    private val settings: Settings = Settings()

    private companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    actual fun isLoggedIn(): Boolean = settings[KEY_IS_LOGGED_IN] ?: false

    actual fun setLoggedIn(value: Boolean) {
        settings[KEY_IS_LOGGED_IN] = value
    }
}

