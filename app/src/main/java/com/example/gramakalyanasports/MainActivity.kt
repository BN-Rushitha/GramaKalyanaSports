package com.example.gramakalyanasports

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val cricketViewModel: CricketViewModel = viewModel()

            NavHost(navController = navController, startDestination = "role_selection") {
                composable("role_selection") {
                    RoleSelectionScreen(onNavigateToSports = { isAdmin ->
                        navController.navigate("sports_selection/$isAdmin")
                    })
                }

                composable("sports_selection/{isAdmin}") { backStackEntry ->
                    val isAdmin = backStackEntry.arguments?.getString("isAdmin") == "true"
                    SportsSelection(
                        onBackClicked = { navController.popBackStack() },
                        onSportSelected = { sport ->
                            if (sport == "Cricket") {
                                if (isAdmin && cricketViewModel.matchStarted) {
                                    navController.navigate("cricket_match/true/${cricketViewModel.teamA}/${cricketViewModel.teamB}")
                                } else if (isAdmin) {
                                    navController.navigate("cricket_setup")
                                } else {
                                    navController.navigate("cricket_match/false/Team A/Team B")
                                }
                            }
                        }
                    )
                }

                composable("cricket_setup") {
                    CricketSetupScreen(
                        onBackClicked = { navController.popBackStack() },
                        onStartMatch = { teamA, teamB, overs ->
                            cricketViewModel.teamA = teamA
                            cricketViewModel.teamB = teamB

                            // EXPLICIT FIX: Treat 'overs' as Any then String to be safe with toIntOrNull
                            val inputOvers = overs.toString()
                            cricketViewModel.totalOversLimit = inputOvers.toIntOrNull() ?: 5

                            cricketViewModel.matchStarted = true
                            navController.navigate("cricket_match/true/$teamA/$teamB")
                        }
                    )
                }

                composable(
                    route = "cricket_match/{isAdmin}/{teamA}/{teamB}",
                    arguments = listOf(
                        navArgument("isAdmin") { type = NavType.BoolType },
                        navArgument("teamA") { type = NavType.StringType },
                        navArgument("teamB") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val isAdmin = backStackEntry.arguments?.getBoolean("isAdmin") ?: false
                    val tA = backStackEntry.arguments?.getString("teamA") ?: "Team A"
                    val tB = backStackEntry.arguments?.getString("teamB") ?: "Team B"

                    CricketMatchPanel(
                        isAdmin = isAdmin,
                        teamAName = tA,
                        teamBName = tB,
                        viewModel = cricketViewModel,
                        onBackClicked = {
                            navController.popBackStack("role_selection", inclusive = false)
                        }
                    )
                }
            }
        }
    }
}