package org.example.project.onboarding.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: String // Replace with actual image resource
)

@Composable
fun OnboardingScreen(
    currentPage: Int,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "We Provide Professional Home services at a very friendly price",
            description = "",
            imageRes = "professional_services"
        ),
        OnboardingPage(
            title = "Easy Service booking & Scheduling",
            description = "",
            imageRes = "easy_booking"
        ),
        OnboardingPage(
            title = "Get Beauty parlor at your home & other Personal Grooming needs",
            description = "",
            imageRes = "beauty_services"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Skip button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onSkipClick) {
                Text(
                    text = "Skip",
                    color = Color(0xFF4A6CF7),
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Image placeholder
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.Gray.copy(alpha = 0.3f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            // Replace with actual image
            Text(
                text = "Image",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Title
        Text(
            text = pages[currentPage].title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Page indicators
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index == currentPage) Color(0xFF4A6CF7)
                            else Color.Gray.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Next button
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A6CF7)
            ),
            shape = CircleShape
        ) {
            Text(
                text = ">",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        currentPage = 0,
        onNextClick = {},
        onSkipClick = {}
    )
}
