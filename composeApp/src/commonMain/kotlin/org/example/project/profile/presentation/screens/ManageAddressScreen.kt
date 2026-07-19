package org.example.project.profile.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_arrow_back
import dukaankonnect.composeapp.generated.resources.ic_delete
import dukaankonnect.composeapp.generated.resources.ic_edit
import dukaankonnect.composeapp.generated.resources.ic_more_vert
import dukaankonnect.composeapp.generated.resources.ic_plus
import org.example.project.profile.domain.model.SavedAddress
import org.example.project.profile.presentation.viewmodels.ManageAddressEffect
import org.example.project.profile.presentation.viewmodels.ManageAddressIntent
import org.example.project.profile.presentation.viewmodels.ManageAddressViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageAddressScreen(
    onBack: () -> Unit = {},
    onNavigateToAddAddress: () -> Unit = {},
    onNavigateToEditAddress: (String) -> Unit = {},
    viewModel: ManageAddressViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val intent: (ManageAddressIntent) -> Unit = viewModel::handleIntent
    var showDeleteDialog by remember { mutableStateOf<SavedAddress?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ManageAddressEffect.NavigateBack -> onBack()
                ManageAddressEffect.NavigateToAddAddress -> onNavigateToAddAddress()
                is ManageAddressEffect.NavigateToEditAddress -> onNavigateToEditAddress(effect.addressId)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { intent(ManageAddressIntent.BackClicked) }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_arrow_back),
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                        Text(
                            text = "Manage Address",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            if (uiState.addresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No addresses found.",
                        color = Color.Gray
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    uiState.addresses.forEach { address ->
                        AddressCard(
                            address = address,
                            onEdit = { intent(ManageAddressIntent.EditAddressClicked(address.id)) },
                            onDelete = { showDeleteDialog = address }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = { intent(ManageAddressIntent.AddAddressClicked) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF6C4DFF),
            contentColor = Color.White
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_plus),
                contentDescription = "Add Address"
            )
        }

        // Delete Confirmation Dialog
        showDeleteDialog?.let { address ->
            AlertDialog(
                onDismissRequest = { showDeleteDialog = null },
                title = {
                    Text("Delete Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                text = {
                    Text("Are you sure you want to delete this address?", fontSize = 15.sp, color = Color.Gray)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            intent(ManageAddressIntent.DeleteAddressClicked(address.id))
                            showDeleteDialog = null
                        }
                    ) {
                        Text("Delete", color = Color.Red, fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = null }) {
                        Text("Cancel", color = Color(0xFF6C4DFF), fontWeight = FontWeight.SemiBold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun AddressCard(
    address: SavedAddress,
    onEdit: (SavedAddress) -> Unit,
    onDelete: (SavedAddress) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Address Label and Default Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            address.label,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        if (address.isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    "Default",
                                    fontSize = 10.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Three dots menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_more_vert),
                                contentDescription = "More",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Edit",
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onEdit(address)
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_edit),
                                        contentDescription = "Edit",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Delete",
                                        fontSize = 14.sp,
                                        color = Color.Red
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDelete(address)
                                },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_delete),
                                        contentDescription = "Delete",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Address Details
                Text(
                    buildString {
                        if (address.houseNumber.isNotBlank()) {
                            append(address.houseNumber)
                        }
                        if (address.street.isNotBlank()) {
                            if (isNotBlank()) append(", ")
                            append(address.street)
                        }
                        if (address.city.isNotBlank()) {
                            if (isNotBlank()) append(", ")
                            append(address.city)
                        }
                        if (address.state.isNotBlank()) {
                            if (isNotBlank()) append(", ")
                            append(address.state)
                        }
                        if (address.landmark.isNotBlank()) {
                            if (isNotBlank()) append(", ")
                            append(address.landmark)
                        }
                    },
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun ManageAddressScreenPreview() {
    ManageAddressScreen()
}
