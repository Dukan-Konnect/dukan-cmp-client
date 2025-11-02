package org.example.project.profile.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "User profile information",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Button(
            onClick = onEditProfileClick,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Edit Profile")
        }

        Button(
            onClick = onSettingsClick,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Settings")
        }

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Logout")
        }
    }
}
