package org.example.project.onboarding.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dukaankonnect.composeapp.generated.resources.*
import org.example.project.onboarding.presentation.viewmodel.AuthEffect
import org.example.project.onboarding.presentation.viewmodel.AuthIntent
import org.example.project.onboarding.presentation.viewmodel.AuthUiState
import org.example.project.onboarding.presentation.viewmodel.AuthViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToOtp: () -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            if (effect is AuthEffect.NavigateToOtpScreen) {
                onNavigateToOtp()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginContent(
            uiState = uiState,
            onAction = { intent -> viewModel.handleIntent(intent) }
        )

        // Unified Dialog rendering
        AuthDialogs(
            dialogState = uiState.dialogState,
            onDismiss = { viewModel.handleIntent(AuthIntent.DismissDialog) }
        )
    }
}

@Composable
fun AuthDialogs(
    dialogState: AuthUiState.DialogState?,
    onDismiss: () -> Unit
) {
    when (dialogState) {
        is AuthUiState.DialogState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(text = "Authentication Error") },
                text = { Text(text = dialogState.message) },
                confirmButton = {
                    TextButton(onClick = onDismiss) {
                        Text("OK", color = Color(0xFF4A6CF7))
                    }
                },
                containerColor = Color.White
            )
        }
        else -> Unit // Loading is handled directly on the button for better UX
    }
}

@Composable
fun LoginContent(
    uiState: AuthUiState,
    onAction: (AuthIntent) -> Unit
) {
    val isLoading = uiState.dialogState == AuthUiState.DialogState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Company Logo",
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "DukanKonnect",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "+91",
                onValueChange = { },
                readOnly = true,
                label = { Text("") },
                modifier = Modifier
                    .width(70.dp)
                    .padding(horizontal = 2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A6CF7),
                    focusedLabelColor = Color(0xFF4A6CF7)
                ),
            )

            Spacer(modifier = Modifier.width(3.dp))

            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = { onAction(AuthIntent.PhoneNumberChanged(it)) },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4A6CF7),
                    focusedLabelColor = Color(0xFF4A6CF7)
                ),
                enabled = !isLoading
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "We will use your phone number for verification\npurpose. For this we will send you OTP.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onAction(AuthIntent.SendOtpClicked) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6CF7)),
            shape = RoundedCornerShape(8.dp),
            enabled = uiState.phoneNumber.length == 10 && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Login/Sign up",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}