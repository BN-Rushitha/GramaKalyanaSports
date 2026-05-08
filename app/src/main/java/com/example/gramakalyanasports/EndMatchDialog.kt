package com.example.gramakalyanasports

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun EndMatchDialog(
    teamAScore: Int,
    teamBScore: Int,
    winner: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🏆 Match Ended 🏆") },
        text = {
            Column {
                Text("Final Score:")
                Text("Team A: $teamAScore", fontWeight = FontWeight.Bold)
                Text("Team B: $teamBScore", fontWeight = FontWeight.Bold)
                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                Text(
                    text = "WINNER: $winner",
                    color = Color(0xFF4CAF50),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}