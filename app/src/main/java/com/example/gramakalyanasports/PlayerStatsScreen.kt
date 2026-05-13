package com.example.gramakalyanasports

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun PlayerStatsScreen(onBackClicked: () -> Unit) {
    val context = LocalContext.current
    var matches by remember { mutableStateOf<List<StoredMatch>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                matches = FirebaseManager.getAllMatches()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Calculate player stats from matches
    val playerStats = matches.flatMap { match ->
        listOf(
            PlayerStat(match.teamAName, match.teamAScore, match.winner == match.teamAName),
            PlayerStat(match.teamBName, match.teamBScore, match.winner == match.teamBName)
        )
    }.groupBy { it.name }
        .map { (name, stats) ->
            Triple(
                name,
                stats.sumOf { it.score.toIntOrNull() ?: 0 },
                stats.count { it.isWinner }
            )
        }
        .sortedByDescending { it.second }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBackClicked) {
            Icon(Icons.Default.ArrowBack, "Back")
        }

        Text("📊 PLAYER STATISTICS", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Career records from all matches", fontSize = 12.sp, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (playerStats.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏆", fontSize = 48.sp)
                    Text("No statistics available", fontSize = 16.sp, color = Color.Gray)
                    Text("Complete matches to see player stats", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Player", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Points", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Wins", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Share", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(playerStats) { (name, points, wins) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, color = Color.White, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
                            Text("$points", color = Color(0xFFFFEB3B), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("$wins", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                            // Share Button for each player
                            Button(
                                onClick = {
                                    val shareText = "🏆 Player Stats 🏆\n\nPlayer: $name\nTotal Points: $points\nTotal Wins: $wins"
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        setPackage("com.whatsapp")
                                    }
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                        }
                                        context.startActivity(Intent.createChooser(fallbackIntent, "Share via"))
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Text("📤", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class PlayerStat(val name: String, val score: String, val isWinner: Boolean)