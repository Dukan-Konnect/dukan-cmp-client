package org.example.project.core.settings

// Expect/Actual wrapper around Multiplatform Settings for simple auth flags
expect class AuthSettings() {
    fun isLoggedIn(): Boolean
    fun setLoggedIn(value: Boolean)
}

