package com.example.gramakalyanasports

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.gramakalyanasports.ui.theme.GramaKalyanaSportsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GramaKalyanaSportsTheme {
                AppScreen()
            }
        }
    }
}

@Composable
fun AppScreen() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.RoleSelection) }
    var selectedRole by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("") }

    when (currentScreen) {
        Screen.RoleSelection -> {
            RoleSelectionScreen { role ->
                selectedRole = role
                currentScreen = Screen.SportsSelection
            }
        }

        Screen.SportsSelection -> {
            SportsSelection(
                onBackClicked = { currentScreen = Screen.RoleSelection },
                onSportSelected = { sport ->
                    selectedSport = sport
                    when (selectedRole) {
                        "Admin" -> currentScreen = Screen.AdminMatchSetup
                        "Player" -> currentScreen = Screen.PlayerStats
                        "Viewer" -> currentScreen = Screen.LiveScores
                    }
                }
            )
        }

        Screen.AdminMatchSetup -> {
            MatchSetupScreen(
                sport = selectedSport,
                onBackClicked = { currentScreen = Screen.SportsSelection },
                onMatchStarted = { currentScreen = Screen.LiveScoring }
            )
        }

        Screen.LiveScoring -> {
            LiveScoringScreen(
                sport = selectedSport,
                onMatchEnd = { currentScreen = Screen.RoleSelection }
            )
        }

        Screen.PlayerStats -> {
            PlayerStatsScreen(
                onBackClicked = { currentScreen = Screen.SportsSelection }
            )
        }

        Screen.LiveScores -> {
            LiveScoresScreen(
                onBackClicked = { currentScreen = Screen.SportsSelection }
            )
        }
    }
}

enum class Screen {
    RoleSelection,
    SportsSelection,
    AdminMatchSetup,
    LiveScoring,
    PlayerStats,
    LiveScores
}