package com.example.gramakalyanasports.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gramakalyanasports.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "tournament_setup"
    ) {
        composable("tournament_setup") {
            TournamentSetupScreen { matchId ->
                navController.navigate("live_scorer/$matchId")
            }
        }

        composable(
            "live_scorer/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            LiveScorerScreen(matchId = matchId)
        }

        composable(
            "fan_view/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            FanViewScreen(matchId = matchId)
        }
    }
}