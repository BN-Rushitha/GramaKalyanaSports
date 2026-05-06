package com.example.gramakalyanasports

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RoleSelectionScreen(onNavigateToSports: (Boolean) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var pinText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Grama-Kalyana Sports", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) { Text("Admin / Scorer", color = Color.White) }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateToSports(false) }, // isAdmin = false
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
            ) { Text("Player / Athlete", color = Color.White) }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateToSports(false) }, // isAdmin = false
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) { Text("Audience / Fan", color = Color.White) }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Admin Access") },
                text = { TextField(value = pinText, onValueChange = { pinText = it }, label = { Text("Enter PIN") }) },
                confirmButton = {
                    Button(onClick = {
                        if (pinText == "2323") {
                            Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                            showDialog = false
                            onNavigateToSports(true) // isAdmin = true
                        } else {
                            Toast.makeText(context, "Incorrect PIN", Toast.LENGTH_SHORT).show()
                        }
                    }) { Text("Verify") }
                },
                dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
            )
        }
    }
}