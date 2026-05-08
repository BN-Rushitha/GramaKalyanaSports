package com.example.gramakalyanasports.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gramakalyanasports.models.SportType
import com.example.gramakalyanasports.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentSetupScreen(
    viewModel: MainViewModel = viewModel(),
    onMatchCreated: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏆 Tournament Setup",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Tournament Name
        OutlinedTextField(
            value = uiState.tournamentName,
            onValueChange = { newName -> viewModel.updateUiState { it.copy(tournamentName = newName) } },
            label = { Text("Tournament Name") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g., Grama Kalyana Cup 2026") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sport Type Selection
        Text("Select Sport", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SportType.values().forEach { sport ->
                FilterChip(
                    selected = uiState.selectedSport == sport,
                    onClick = { viewModel.updateUiState { it.copy(selectedSport = sport) } },
                    label = { Text(sport.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Team A
        OutlinedTextField(
            value = uiState.teamAName,
            onValueChange = { viewModel.updateUiState { it.copy(teamAName = it) } },
            label = { Text("Team A Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Team B
        OutlinedTextField(
            value = uiState.teamBName,
            onValueChange = { viewModel.updateUiState { it.copy(teamBName = it) } },
            label = { Text("Team B Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.createMatch()
                onMatchCreated(viewModel.currentMatch.value?.matchId ?: "")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.tournamentName.isNotBlank() &&
                    uiState.teamAName.isNotBlank() &&
                    uiState.teamBName.isNotBlank()
        ) {
            Text("Start Match 🎯")
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}