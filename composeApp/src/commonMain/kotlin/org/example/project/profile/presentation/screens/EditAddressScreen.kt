package org.example.project.profile.presentation.screens


import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import dukaankonnect.composeapp.generated.resources.Res
import dukaankonnect.composeapp.generated.resources.ic_arrow_back
import org.example.project.profile.presentation.viewmodels.EditAddressEffect
import org.example.project.profile.presentation.viewmodels.EditAddressIntent
import org.example.project.profile.presentation.viewmodels.EditAddressViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditAddressScreen(
    onBack: () -> Unit = {},
    viewModel: EditAddressViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val intent: (EditAddressIntent) -> Unit = viewModel::handleIntent

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                EditAddressEffect.NavigateBack -> onBack()
            }
        }
    }

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
                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { intent(EditAddressIntent.BackClicked) }) {
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // House/Flat/Block Number
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "House/Flat/Block Number",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = uiState.houseNumber,
                                onValueChange = { intent(EditAddressIntent.HouseNumberChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6C4DFF),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Street",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = uiState.street,
                                onValueChange = { intent(EditAddressIntent.StreetChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6C4DFF),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "City",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = uiState.city,
                                onValueChange = { intent(EditAddressIntent.CityChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6C4DFF),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "State",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = uiState.state,
                                onValueChange = { intent(EditAddressIntent.StateChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6C4DFF),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Landmark (Optional)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Landmark (Optional)",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = uiState.landmark,
                                onValueChange = { intent(EditAddressIntent.LandmarkChanged(it)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6C4DFF),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Save as
                        Text(
                            "Save as",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SaveAsChip(
                                label = "Home",
                                isSelected = uiState.saveAs == "Home",
                                onClick = { intent(EditAddressIntent.SaveAsChanged("Home")) },
                                modifier = Modifier.weight(1f)
                            )
                            SaveAsChip(
                                label = "Other",
                                isSelected = uiState.saveAs == "Other",
                                onClick = { intent(EditAddressIntent.SaveAsChanged("Other")) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Set as default address",
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Only one address can be default at a time.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = uiState.isDefault,
                                onCheckedChange = { intent(EditAddressIntent.DefaultChanged(it)) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF6C4DFF),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFE0E0E0)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Update Address Button
                        Button(
                            onClick = { intent(EditAddressIntent.SaveClicked) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C4DFF)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !uiState.isLoading &&
                                uiState.houseNumber.isNotBlank() &&
                                uiState.street.isNotBlank() &&
                                uiState.city.isNotBlank() &&
                                uiState.state.isNotBlank()
                        ) {
                            Text(
                                "Update address",
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
}

@Composable
fun SaveAsChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0xFFEDE7F6)
                else Color.White
            )
            .border(
                width = 1.5.dp,
                color = if (isSelected) Color(0xFF6C4DFF) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 15.sp,
            color = if (isSelected) Color(0xFF6C4DFF) else Color.Black,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Preview
@Composable
fun EditAddressScreenPreview() {
    EditAddressScreen()
}
