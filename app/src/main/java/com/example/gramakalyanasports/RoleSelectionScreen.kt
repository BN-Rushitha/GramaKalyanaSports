package com.example.gramakalyanasports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (String) -> Unit
) {
    var showPinDialog by remember { mutableStateOf(false) }
    var selectedRoleName by remember { mutableStateOf("") }
    var enteredPin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🏏 GRAMA KALYANA SPORTS 🏐",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Admin Card
        RoleCard(
            roleName = "ADMIN",
            roleIcon = "🔐",
            description = "Full control: Create matches, update scores",
            backgroundColor = Color(0xFFD32F2F)
        ) {
            selectedRoleName = "Admin"
            showPinDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Player Card
        RoleCard(
            roleName = "PLAYER",
            roleIcon = "👥",
            description = "View your stats and career records",
            backgroundColor = Color(0xFF1976D2)
        ) {
            onRoleSelected("Player")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Viewer Card
        RoleCard(
            roleName = "VIEWER",
            roleIcon = "👁️",
            description = "Follow live scores from anywhere",
            backgroundColor = Color(0xFFF57C00)
        ) {
            onRoleSelected("Viewer")
        }
    }

    // PIN Dialog for Admin
    if (showPinDialog) {
        Dialog(onDismissRequest = { showPinDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔐 Admin PIN Required",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                enteredPin = it
                                showError = false
                            }
                        },
                        label = { Text("Enter 4-digit PIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = showError,
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (showError) {
                                Text("Invalid PIN! Try again.")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showPinDialog = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (enteredPin == "1234") { // Default Admin PIN
                                    onRoleSelected(selectedRoleName)
                                    showPinDialog = false
                                    enteredPin = ""
                                } else {
                                    showError = true
                                    enteredPin = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Verify")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    roleName: String,
    roleIcon: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = roleIcon,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = roleName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}