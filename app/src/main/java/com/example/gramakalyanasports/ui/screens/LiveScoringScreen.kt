package com.example.gramakalyanasports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LiveScoringScreen(
    sport: String,
    onMatchEnd: () -> Unit
) {
    var teamAScore by remember { mutableIntStateOf(0) }
    var teamBScore by remember { mutableIntStateOf(0) }
    var showEndMatchDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Scoreboard Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚡ LIVE SCORE ⚡",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sport,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Team Scores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreCard(
                teamName = "Team A",
                score = teamAScore,
                onAddPoint = { teamAScore++ },
                onSubtractPoint = { if (teamAScore > 0) teamAScore-- }
            )

            ScoreCard(
                teamName = "Team B",
                score = teamBScore,
                onAddPoint = { teamBScore++ },
                onSubtractPoint = { if (teamBScore > 0) teamBScore-- }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sport-specific scoring buttons
        when (sport) {
            "Cricket" -> CricketScoringButtons(
                onScoreUpdate = { points ->
                    teamAScore += points
                }
            )
            "Kabaddi" -> KabaddiScoringButtons(
                onScoreUpdate = { points ->
                    teamAScore += points
                }
            )
            "Volleyball" -> VolleyballScoringButtons(
                onScoreUpdate = { points ->
                    teamAScore += points
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // End Match Button
        Button(
            onClick = { showEndMatchDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
            Text("🏁 End Match")
        }
    }

    // End Match Dialog
    if (showEndMatchDialog) {
        EndMatchDialog(
            teamAScore = teamAScore,
            teamBScore = teamBScore,
            winner = if (teamAScore > teamBScore) "Team A" else if (teamBScore > teamAScore) "Team B" else "Tie",
            onConfirm = {
                showEndMatchDialog = false
                onMatchEnd()
            },
            onDismiss = { showEndMatchDialog = false }
        )
    }
}

@Composable
fun ScoreCard(
    teamName: String,
    score: Int,
    onAddPoint: () -> Unit,
    onSubtractPoint: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = teamName,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = score.toString(),
                color = Color(0xFFFFEB3B),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onSubtractPoint, modifier = Modifier.size(48.dp)) {
                    Text("-", fontSize = 20.sp)
                }
                Button(onClick = onAddPoint, modifier = Modifier.size(48.dp)) {
                    Text("+", fontSize = 20.sp)
                }
            }
        }
    }
}

@Composable
fun CricketScoringButtons(onScoreUpdate: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "🏏 CRICKET SCORING 🏏",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val runs = listOf(1, 2, 4, 6)
            items(runs.size) { index ->
                Button(
                    onClick = { onScoreUpdate(runs[index]) },
                    modifier = Modifier.size(70.dp, 60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(
                        text = "+${runs[index]}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // NB (No Ball)
            item {
                Button(
                    onClick = { onScoreUpdate(1) }, // No ball gives 1 run
                    modifier = Modifier.size(70.dp, 60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("NB", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // WD (Wide)
            item {
                Button(
                    onClick = { onScoreUpdate(1) }, // Wide gives 1 run
                    modifier = Modifier.size(70.dp, 60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Text("WD", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Manual Entry
            var showManualDialog by remember { mutableStateOf(false) }
            item {
                Button(
                    onClick = { showManualDialog = true },
                    modifier = Modifier.size(70.dp, 60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                ) {
                    Text("📝", fontSize = 20.sp)
                }
            }

            // Manual runs dialog
            if (showManualDialog) {
                AlertDialog(
                    onDismissRequest = { showManualDialog = false },
                    title = { Text("Manual Runs Entry") },
                    text = {
                        var manualRuns by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = manualRuns,
                            onValueChange = { if (it.all { char -> char.isDigit() }) manualRuns = it },
                            label = { Text("Enter runs") }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val runs = manualRuns.toIntOrNull()
                                if (runs != null && runs > 0) {
                                    onScoreUpdate(runs)
                                }
                                showManualDialog = false
                            }
                        ) {
                            Text("Add")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showManualDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        Text(
            text = "Note: NB & WD add 1 run + extra ball (not implemented in basic version)",
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun KabaddiScoringButtons(onScoreUpdate: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "🤼 KABADDI SCORING 🤼",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Button(onClick = { onScoreUpdate(1) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("Raid\nPoint", fontSize = 14.sp)
                }
            }
            item {
                Button(onClick = { onScoreUpdate(2) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("Bonus\nPoint", fontSize = 14.sp)
                }
            }
            item {
                Button(onClick = { onScoreUpdate(3) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("All Out\n+3", fontSize = 14.sp)
                }
            }
            item {
                Button(onClick = { onScoreUpdate(1) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("Tackle\nPoint", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun VolleyballScoringButtons(onScoreUpdate: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "🏐 VOLLEYBALL SCORING 🏐",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Button(onClick = { onScoreUpdate(1) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("Point", fontSize = 16.sp)
                }
            }
            item {
                Button(onClick = { onScoreUpdate(3) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("Spike\n+3", fontSize = 14.sp)
                }
            }
            item {
                Button(onClick = { onScoreUpdate(2) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("Block\n+2", fontSize = 14.sp)
                }
            }
            item {
                Button(onClick = { onScoreUpdate(1) }, modifier = Modifier.size(70.dp, 60.dp)) {
                    Text("Ace\nServe", fontSize = 14.sp)
                }
            }
        }
    }
}