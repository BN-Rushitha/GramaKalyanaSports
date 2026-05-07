package com.example.gramakalyanasports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CricketMatchPanel(
    isAdmin: Boolean,
    teamAName: String,
    teamBName: String,
    onBackClicked: () -> Unit,
    onEndMatchTriggered: () -> Unit, // ADDED THIS LINE
    viewModel: CricketViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // --- TOP BAR ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClicked) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Live Match",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- THE "CATCH": EXTRA RUNS DIALOG ---
        if (viewModel.showExtraDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showExtraDialog = false },
                title = { Text("Extra Runs (${viewModel.pendingExtraType})") },
                text = { Text("Enter additional runs taken:") },
                confirmButton = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        (0..4).forEach { run ->
                            TextButton(onClick = { viewModel.resolveExtra(run) }) {
                                Text("$run")
                            }
                        }
                    }
                }
            )
        }

        // --- LIVE MATCH CARD ---
        Card(
            modifier = Modifier.fillMaxWidth().height(210.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("$teamAName vs $teamBName", color = Color.White, fontSize = 20.sp)

                Text(
                    "${viewModel.totalRuns} / ${viewModel.totalWickets}",
                    color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold
                )

                Text("Overs: ${viewModel.getOversDisplay()}", color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)

                if (viewModel.isFreeHit) {
                    Text("FREE HIT!", color = Color.Yellow, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                CurrentOverBalls(balls = viewModel.currentOverBalls)
            }
        }

        // --- ADMIN SCORING CONTROLS ---
        if (isAdmin) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Scorer Dashboard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextButton(onClick = { viewModel.undoLastAction() }) {
                    Text("UNDO", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            // Legal Run Buttons
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { viewModel.recordLegalBall(0) }) { Text("0") }
                Button(onClick = { viewModel.recordLegalBall(1) }) { Text("1") }
                Button(onClick = { viewModel.recordLegalBall(4) }) { Text("4") }
                Button(onClick = { viewModel.recordLegalBall(6) }) { Text("6") }
            }

            // Extras, Wickets
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { viewModel.openExtraPrompt("WD") }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) { Text("WD") }
                Button(onClick = { viewModel.openExtraPrompt("NB") }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) { Text("NB") }
                Button(onClick = { viewModel.recordLegalBall(0, isWicket = true) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("WKT") }
            }

            // Manual Entry
            OutlinedButton(
                onClick = { viewModel.openExtraPrompt("Manual") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Enter Runs Manually (e.g. 3 runs)")
            }

            // --- UNIVERSAL END MATCH BUTTON ---
            Button(
                onClick = { onEndMatchTriggered() },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Red color for end action
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("END MATCH", color = Color.White, fontWeight = FontWeight.Bold)
            }

        } else {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Match in Progress - Live Updates only", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Past Matches", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.weight(1f)) {
            item { PastMatchItem("Team C vs Team D", "Team C won by 10 runs") }
            item { PastMatchItem("Team X vs Team Y", "Team Y won by 5 wickets") }
        }
    }
}

@Composable
fun CurrentOverBalls(balls: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        balls.forEach { ball ->
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(19.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = ball, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PastMatchItem(teams: String, result: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f))
    ) {
        ListItem(
            headlineContent = { Text(teams, fontWeight = FontWeight.Bold) },
            supportingContent = { Text(result) }
        )
    }
}