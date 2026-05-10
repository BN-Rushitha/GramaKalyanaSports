package com.example.gramakalyanasports

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MatchSetupScreen(
    sport: String,
    onBackClicked: () -> Unit,
    onMatchStarted: (battingTeam: String, bowlingTeam: String, overs: Float) -> Unit
) {
    var battingTeamName by remember { mutableStateOf("") }
    var bowlingTeamName by remember { mutableStateOf("") }
    var tournamentName by remember { mutableStateOf("") }
    var oversInput by remember { mutableStateOf("10") }
    var oversError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBackClicked) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Setup $sport Match 🏆",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = tournamentName,
                onValueChange = { tournamentName = it },
                label = { Text("Tournament Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = battingTeamName,
                onValueChange = { battingTeamName = it },
                label = { Text("Batting Team Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = bowlingTeamName,
                onValueChange = { bowlingTeamName = it },
                label = { Text("Bowling Team Name") },
                modifier = Modifier.fillMaxWidth()
            )

            if (sport == "Cricket") {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = oversInput,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty()) {
                            oversInput = newValue
                            oversError = false
                        } else {
                            val intValue = newValue.toIntOrNull()
                            if (intValue != null && intValue in 1..50) {
                                oversInput = newValue
                                oversError = false
                            } else {
                                oversError = true
                            }
                        }
                    },
                    label = { Text("Number of Overs (1-50)") },
                    isError = oversError,
                    supportingText = {
                        if (oversError) {
                            Text("Please enter overs between 1 and 50")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val overs = oversInput.toFloatOrNull() ?: 10f
                    onMatchStarted(battingTeamName, bowlingTeamName, overs)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = tournamentName.isNotBlank() &&
                        battingTeamName.isNotBlank() &&
                        bowlingTeamName.isNotBlank()
            ) {
                Text("Start Match 🎯")
            }
        }
    }
}