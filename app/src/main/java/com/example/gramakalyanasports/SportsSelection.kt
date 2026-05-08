package com.example.gramakalyanasports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SportsSelection(onBackClicked: () -> Unit, onSportSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar with Back Button
        IconButton(onClick = onBackClicked) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Sport",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20) // Deep Green for a sports feel
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sport 1: Cricket
            SportCard(
                name = "CRICKET",
                color = Color(0xFFD32F2F), // Red for Cricket ball
                description = "Manage Overs, Runs & Wickets",
                onClick = { onSportSelected("Cricket") }
            )

            // Sport 2: Kabaddi
            SportCard(
                name = "KABADDI",
                color = Color(0xFFF57C00), // Orange for energy/mat
                description = "Track Raids, Points & Tackles",
                onClick = { onSportSelected("Kabaddi") }
            )

            // Sport 3: Volleyball
            SportCard(
                name = "VOLLEYBALL",
                color = Color(0xFF1976D2), // Blue for Court
                description = "Set scores & Service turns",
                onClick = { onSportSelected("Volleyball") }
            )
        }
    }
}

@Composable
fun SportCard(name: String, color: Color, description: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}