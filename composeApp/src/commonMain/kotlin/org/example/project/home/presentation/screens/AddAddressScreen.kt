package org.example.project.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_arrow_back
import dukaankonnect.composeapp.generated.resources.ic_location
import org.example.project.core.utils.location.MapView
import org.example.project.home.presentation.viewmodels.AddAddressUiState
import org.example.project.home.presentation.viewmodels.AddAddressViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddAddressScreen(
    onSaveAndProceed: () -> Unit = {},
    onClose: () -> Unit = {},
    viewModel: AddAddressViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AddAddressContent(
        uiState = uiState,
        onSaveAndProceed = { viewModel.saveAddress(onSaveAndProceed) },
        onClose = onClose,
        onFetchLocation = viewModel::fetchLocation,
        onUpdateLocation = viewModel::updateLocation,
        onHouseNumberChange = viewModel::updateHouseNumber,
        onLandmarkChange = viewModel::updateLandmark
    )
}

@Composable
fun AddAddressContent(
    uiState: AddAddressUiState,
    onSaveAndProceed: () -> Unit,
    onClose: () -> Unit,
    onFetchLocation: () -> Unit,
    onUpdateLocation: (Double, Double) -> Unit = { _, _ -> },
    onHouseNumberChange: (String) -> Unit = {},
    onLandmarkChange: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onClose) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_arrow_back),
                                    contentDescription = "Back",
                                    tint = Color.Black
                                )
                            }
                            Text(
                                text = "Add address",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE8EAF6))
            ) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF6C4DFF))
                    }
                } else {
                    MapView(
                        modifier = Modifier.fillMaxSize(),
                        latitude = uiState.latitude,
                        longitude = uiState.longitude,
                        onCameraPositionChanged = onUpdateLocation
                    )
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(
                                Color(0xFF6C4DFF).copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    Color(0xFF6C4DFF).copy(alpha = 0.25f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_location),
                                contentDescription = "Location",
                                tint = Color(0xFF6C4DFF),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 100.dp)
                        .padding(horizontal = 32.dp),
                    color = Color(0xFF424242),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Your professional will arrive here",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Location Target Button (bottom right)
                FloatingActionButton(
                    onClick = onFetchLocation,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(48.dp),
                    containerColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_location),
                        contentDescription = "My Location",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Bottom Sheet with Address Details
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(2.dp))
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Address Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                uiState.streetAddress.ifBlank { "Current Location" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                uiState.formattedAddress.ifBlank { "Fetching location..." },
                                fontSize = 13.sp,
                                color = Color.Gray,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // House/Flat Number Input
                    OutlinedTextField(
                        value = uiState.houseNumber,
                        onValueChange = onHouseNumberChange,
                        label = {
                            Text(
                                "House/Flat Number",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6C4DFF),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF6C4DFF),
                            unfocusedLabelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Landmark Input
                    OutlinedTextField(
                        value = uiState.landmark,
                        onValueChange = onLandmarkChange,
                        label = {
                            Text(
                                "Landmark (Optional)",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6C4DFF),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedLabelColor = Color(0xFF6C4DFF),
                            unfocusedLabelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save and Proceed Button
                    Button(
                        onClick = onSaveAndProceed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C4DFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Save",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
