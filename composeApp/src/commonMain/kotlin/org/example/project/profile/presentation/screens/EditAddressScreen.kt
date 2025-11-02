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
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EditAddressScreen(
    onBack: () -> Unit = {},
    onUpdateAddress: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    addressId: String = "",
    initialLocation: String = "Madhapur, Hyderabad",
    initialFullAddress: String = "Plot no.209, Kavuri Hills, Madhapur, Telangana 500033",
    initialHouseNumber: String = "Plot no.209",
    initialLandmark: String = "",
    initialSaveAs: String = "Home"
) {
    var location by remember { mutableStateOf(initialLocation) }
    var fullAddress by remember { mutableStateOf(initialFullAddress) }
    var houseNumber by remember { mutableStateOf(initialHouseNumber) }
    var landmark by remember { mutableStateOf(initialLandmark) }
    var saveAs by remember { mutableStateOf(initialSaveAs) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Status Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column {
                    // Status Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "9:41",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Signal",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "WiFi",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Icon(
                                imageVector = AppIcons.placeholder,
                                contentDescription = "Battery",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = AppIcons.placeholder,
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
                // Map Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color(0xFFE8EAF6))
                ) {
                    // Map placeholder with pin
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.placeholder,
                            contentDescription = "Location",
                            tint = Color(0xFF6C4DFF),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    // Location Target Button (bottom right)
                    FloatingActionButton(
                        onClick = { /* Re-center map */ },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(48.dp),
                        containerColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = AppIcons.placeholder,
                            contentDescription = "My Location",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Form Section
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
                        // Location Header
                        Text(
                            location,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            fullAddress,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // House/Flat/Block Number
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "House/Flat/Block Number",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            OutlinedTextField(
                                value = houseNumber,
                                onValueChange = { houseNumber = it },
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
                                value = landmark,
                                onValueChange = { landmark = it },
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
                                isSelected = saveAs == "Home",
                                onClick = { saveAs = "Home" },
                                modifier = Modifier.weight(1f)
                            )
                            SaveAsChip(
                                label = "Other",
                                isSelected = saveAs == "Other",
                                onClick = { saveAs = "Other" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Update Address Button
                        Button(
                            onClick = {
                                onUpdateAddress(houseNumber, landmark, saveAs, fullAddress)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C4DFF)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(8.dp)
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