package org.example.project.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.core.resources.AppIcons
import org.example.project.home.presentation.viewmodels.ProfileViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onEditProfileClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8EAF6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.personLarge,
                        contentDescription = "Profile",
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFF6C4DFF)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name and Phone
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = state.name ?: "Guest User",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.phoneNumber?.let { "+91 $it" } ?: "No phone number",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Edit Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6C4DFF))
                        .clickable(onClick = onEditProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items
            ProfileMenuItem(
                icon = AppIcons.address,
                title = "Manage Address",
                onClick = { /* TODO */ }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            ProfileMenuItem(
                icon = AppIcons.share,
                title = "Refer & Earn",
                onClick = { /* TODO */ }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            ProfileMenuItem(
                icon = AppIcons.star,
                title = "Rate us",
                onClick = { /* TODO */ }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            ProfileMenuItem(
                icon = AppIcons.info,
                title = "About mHome Services",
                onClick = { /* TODO */ }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            ProfileMenuItem(
                icon = AppIcons.logout,
                title = "Logout",
                onClick = { /* TODO */ }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = AppIcons.arrowForward,
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

