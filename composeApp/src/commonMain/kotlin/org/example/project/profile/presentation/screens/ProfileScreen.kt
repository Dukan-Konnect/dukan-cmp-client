package org.example.project.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_address
import dukaankonnect.composeapp.generated.resources.ic_arrow_forward
import dukaankonnect.composeapp.generated.resources.ic_edit
import dukaankonnect.composeapp.generated.resources.ic_info
import dukaankonnect.composeapp.generated.resources.ic_logout
import dukaankonnect.composeapp.generated.resources.ic_person_large
import dukaankonnect.composeapp.generated.resources.ic_share
import dukaankonnect.composeapp.generated.resources.ic_star
import kotlinx.coroutines.launch
import org.example.project.home.presentation.viewmodels.ProfileEffect
import org.example.project.home.presentation.viewmodels.ProfileIntent
import org.example.project.home.presentation.viewmodels.ProfileViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    onEditProfileClick: () -> Unit = {},
    onNavigateToManageAddress: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val intent: (ProfileIntent) -> Unit = viewModel::handleIntent

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.NavigateToManageAddress -> onNavigateToManageAddress() // Added
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 22.sp,
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
            Spacer(modifier = Modifier.height(4.dp))

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
                        painter = painterResource(Res.drawable.ic_person_large),
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
                        painter = painterResource(Res.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items
            ProfileMenuItem(
                icon = Res.drawable.ic_address,
                title = "Manage Address",
                onClick = { intent(ProfileIntent.ManageAddressClicked) }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            ProfileMenuItem(
                icon = Res.drawable.ic_share,
                title = "Refer & Earn",
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = "Thank you for using DukaanKonnect")
                    }
                }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            ProfileMenuItem(
                icon = Res.drawable.ic_star,
                title = "Rate us",
                onClick = { /* TODO */ }
            )

            Divider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            ProfileMenuItem(
                icon = Res.drawable.ic_info,
                title = "About DukanKonnect",
                onClick = { /* TODO */ }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = Color(0xFFE0E0E0)
            )

            ProfileMenuItem(
                icon = Res.drawable.ic_logout,
                title = "Logout",
                onClick = { intent(ProfileIntent.LogoutClicked) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: DrawableResource,
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
            painter = painterResource(icon),
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
            painter = painterResource(Res.drawable.ic_arrow_forward),
            contentDescription = "Navigate",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

