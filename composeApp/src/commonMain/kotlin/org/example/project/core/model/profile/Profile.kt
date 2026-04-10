package org.example.project.core.model.profile

data class Profile(
    val fullName: String,
    val displayEmail: String,
    val formattedPhone: String,
    val canUpdate: Boolean
)