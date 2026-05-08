package com.example.gramakalyanasports.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gramakalyanasports.viewmodel.MainViewModel

@Composable
fun FanViewScreen(
    matchId: String,
    viewModel: MainViewModel = viewModel()
) {
    val currentMatch by viewModel.currentMatch.collectAsState()

    LaunchedEffect(matchId) {
        viewModel.subscribeToLiveMatch(matchId)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (currentMatch == null) {
            CircularProgressIndicator()
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📡 LIVE FROM ${currentMatch?.tournamentName?.uppercase()}",
                        color = Color(0xFFFF5722),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Large Score Display for Outdoor Visibility
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentMatch?.teamA?.teamName ?: "Team A",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentMatch?.currentScore?.teamAScore?.toString() ?: "0",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFEB3B)
                            )
                        }

                        Text(
                            text = "VS",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = currentMatch?.teamB?.teamName ?: "Team B",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentMatch?.currentScore?.teamBScore?.toString() ?: "0",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFEB3B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Last Updated Timestamp
                    Text(
                        text = "Last updated: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(currentMatch?.currentScore?.lastUpdated ?: 0))}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "🔄 Updates in real-time",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}