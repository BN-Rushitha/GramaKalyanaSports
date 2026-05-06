package com.example.gramakalyanasports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CricketSetupScreen(
    onBackClicked: () -> Unit,
    onStartMatch: (String, String, Int) -> Unit // Fixed parameter definition
) {
    var teamA by remember { mutableStateOf("") }
    var teamB by remember { mutableStateOf("") }
    var totalOvers by remember { mutableStateOf("5") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Setup") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = teamA,
                onValueChange = { teamA = it },
                label = { Text("Batting Team Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = totalOvers,
                onValueChange = { if (it.all { char -> char.isDigit() }) totalOvers = it },
                label = { Text("Number of Overs") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = teamB,
                onValueChange = { teamB = it },
                label = { Text("Bowling Team Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                // FIXED: Use lowercase teamA and teamB to match the variables above
                onClick = { onStartMatch(teamA, teamB, totalOvers.toIntOrNull() ?: 5) },
                modifier = Modifier.fillMaxWidth(),
                enabled = teamA.isNotBlank() && teamB.isNotBlank() && totalOvers.isNotBlank()
            ) {
                Text("Start Cricket Match")
            }
        }
    }
}