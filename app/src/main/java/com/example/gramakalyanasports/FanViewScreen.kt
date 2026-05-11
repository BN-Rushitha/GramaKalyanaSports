package com.example.gramakalyanasports

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun FanViewScreen(onBackClicked: () -> Unit) {
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBackClicked) {
            Icon(Icons.Default.ArrowBack, "Back")
        }

        Text("📡 PAST MATCHES", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("View all completed matches", fontSize = 12.sp, color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (matches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏏", fontSize = 48.sp)
                    Text("No past matches found", fontSize = 16.sp, color = Color.Gray)
                    Text("Complete a match to see it here", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(matches) { match ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("🏆 ${match.sport}", fontWeight = FontWeight.Bold, color = Color(0xFFFFEB3B))
                                Text(
                                    java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(match.timestamp),
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("${match.teamAName} vs ${match.teamBName}", fontSize = 14.sp, color = Color.White)
                            Text("Score: ${match.teamAScore} - ${match.teamBScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                            Text("Winner: ${match.winner}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}