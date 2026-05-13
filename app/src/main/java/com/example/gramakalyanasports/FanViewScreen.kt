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
fun FanViewScreen(onBackClicked: () -> Unit) {
    val context = LocalContext.current
    var liveMatches by remember { mutableStateOf<List<StoredMatch>>(emptyList()) }
    var cricketMatches by remember { mutableStateOf<List<StoredMatch>>(emptyList()) }
    var volleyballMatches by remember { mutableStateOf<List<StoredMatch>>(emptyList()) }
    var kabaddiMatches by remember { mutableStateOf<List<StoredMatch>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Live, 1 = Past
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val allMatches = FirebaseManager.getAllMatches()
            liveMatches = allMatches.filter { it.live }
            println("📡 Live matches found: ${liveMatches.size}")
            cricketMatches = allMatches.filter { it.sport == "Cricket" && !it.live }
            volleyballMatches = allMatches.filter { it.sport == "Volleyball" && !it.live }
            kabaddiMatches = allMatches.filter { it.sport == "Kabaddi" && !it.live }
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBackClicked) {
            Icon(Icons.Default.ArrowBack, "Back")
        }

        Text("📡 FAN VIEW", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(8.dp))

        // Tab Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 0) Color(0xFF1B5E20) else Color(0xFF263238),
                    contentColor = if (selectedTab == 0) Color(0xFFFFEB3B) else Color.White
                )
            ) {
                Text("🔴 LIVE", fontSize = 14.sp)
            }
            Button(
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == 1) Color(0xFF1B5E20) else Color(0xFF263238),
                    contentColor = if (selectedTab == 1) Color(0xFFFFEB3B) else Color.White
                )
            ) {
                Text("📋 PAST", fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (selectedTab == 0) {
            // LIVE MATCHES
            if (liveMatches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No live matches", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(liveMatches) { match ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🏆 ${match.sport}", color = Color(0xFFFFEB3B), fontWeight = FontWeight.Bold)
                                Text("${match.teamAName} vs ${match.teamBName}", color = Color.White)
                                Text("Score: ${match.teamAScore} - ${match.teamBScore}", color = Color(0xFFFFEB3B), fontWeight = FontWeight.Bold)
                                Text("⚡ LIVE", fontSize = 10.sp, color = Color.Red)

                                // Share Button for Live Match
                                Button(
                                    onClick = {
                                        val shareText = "🏆 LIVE MATCH 🏆\n\n${match.teamAName} vs ${match.teamBName}\nSport: ${match.sport}\nCurrent Score: ${match.teamAScore} - ${match.teamBScore}"
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
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                ) {
                                    Text("📤 Share Live Match", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // PAST MATCHES grouped by sport
            Column {
                if (cricketMatches.isNotEmpty()) {
                    Text("🏏 CRICKET", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    cricketMatches.forEach { match ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(match.sport, fontWeight = FontWeight.Bold, color = Color(0xFFFFEB3B))
                                    Text(java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(match.timestamp), fontSize = 10.sp, color = Color.Gray)
                                }
                                Text("${match.teamAName} vs ${match.teamBName}", color = Color.White)
                                Text("Score: ${match.teamAScore} - ${match.teamBScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("Winner: ${match.winner}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))

                                // Share Button for Past Match
                                Button(
                                    onClick = {
                                        val shareText = "🏆 MATCH RESULT 🏆\n\n${match.teamAName} vs ${match.teamBName}\nSport: ${match.sport}\nFinal Score: ${match.teamAScore} - ${match.teamBScore}\nWinner: ${match.winner}"
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
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                ) {
                                    Text("📤 Share Result", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (volleyballMatches.isNotEmpty()) {
                    Text("🏐 VOLLEYBALL", fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                    volleyballMatches.forEach { match ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(match.sport, fontWeight = FontWeight.Bold, color = Color(0xFFFFEB3B))
                                    Text(java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(match.timestamp), fontSize = 10.sp, color = Color.Gray)
                                }
                                Text("${match.teamAName} vs ${match.teamBName}", color = Color.White)
                                Text("Score: ${match.teamAScore} - ${match.teamBScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("Winner: ${match.winner}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))

                                // Share Button for Volleyball Match
                                Button(
                                    onClick = {
                                        val shareText = "🏐 VOLLEYBALL RESULT 🏐\n\n${match.teamAName} vs ${match.teamBName}\nFinal Score: ${match.teamAScore} - ${match.teamBScore}\nWinner: ${match.winner}"
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
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                ) {
                                    Text("📤 Share Result", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
                if (kabaddiMatches.isNotEmpty()) {
                    Text("🤼 KABADDI", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                    kabaddiMatches.forEach { match ->
                        Card(modifier = Modifier.fillMaxWidth().padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(match.sport, fontWeight = FontWeight.Bold, color = Color(0xFFFFEB3B))
                                    Text(java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(match.timestamp), fontSize = 10.sp, color = Color.Gray)
                                }
                                Text("${match.teamAName} vs ${match.teamBName}", color = Color.White)
                                Text("Score: ${match.teamAScore} - ${match.teamBScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                                Text("Winner: ${match.winner}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))

                                // Share Button for Kabaddi Match
                                Button(
                                    onClick = {
                                        val shareText = "🤼 KABADDI RESULT 🤼\n\n${match.teamAName} vs ${match.teamBName}\nFinal Score: ${match.teamAScore} - ${match.teamBScore}\nWinner: ${match.winner}"
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
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                                ) {
                                    Text("📤 Share Result", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                if (cricketMatches.isEmpty() && volleyballMatches.isEmpty() && kabaddiMatches.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No past matches", color = Color.Gray)
                    }
                }
            }
        }
    }
}