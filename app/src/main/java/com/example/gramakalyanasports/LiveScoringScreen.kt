package com.example.gramakalyanasports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class MatchState(
    val innings: Int,
    val aRuns: Int,
    val aWickets: Int,
    val bRuns: Int,
    val bWickets: Int,
    val overs: Float,
    val ballCount: Int,
    val target: Int,
    val history: List<String>
)

@Composable
fun LiveScoringScreen(
    sport: String,
    teamAName: String = "Team A",
    teamBName: String = "Team B",
    oversLimit: Float = 10f,
    onMatchEnd: () -> Unit
) {
    // ==================== CRICKET STATE ====================
    var currentInnings by remember { mutableIntStateOf(1) }
    var teamARuns by remember { mutableIntStateOf(0) }
    var teamAWickets by remember { mutableIntStateOf(0) }
    var teamBRuns by remember { mutableIntStateOf(0) }
    var teamBWickets by remember { mutableIntStateOf(0) }
    var currentOvers by remember { mutableFloatStateOf(0f) }
    var ballCounter by remember { mutableIntStateOf(0) }
    var targetRuns by remember { mutableIntStateOf(0) }
    var matchResult by remember { mutableStateOf<String?>(null) }
    var showEndMatchDialog by remember { mutableStateOf(false) }
    var ballHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var previousState by remember { mutableStateOf<MatchState?>(null) }
    var isFreeHit by remember { mutableStateOf(false) }
    var showNoBallDialog by remember { mutableStateOf(false) }
    var noBallRuns by remember { mutableStateOf("") }

    // ==================== VOLLEYBALL STATE ====================
    var teamAPoints by remember { mutableIntStateOf(0) }
    var teamBPoints by remember { mutableIntStateOf(0) }
    var teamASets by remember { mutableIntStateOf(0) }
    var teamBSets by remember { mutableIntStateOf(0) }
    var servingTeam by remember { mutableIntStateOf(1) }

    // ==================== KABADDI STATE ====================
    var kabTeamAPoints by remember { mutableIntStateOf(0) }
    var kabTeamBPoints by remember { mutableIntStateOf(0) }
    var raidingTeam by remember { mutableIntStateOf(1) }
    var matchTimeSeconds by remember { mutableIntStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var targetScore by remember { mutableStateOf("30") }
    var showTargetDialog by remember { mutableStateOf(false) }

    // Timer effect
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            matchTimeSeconds++
        }
    }

    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    // ==================== CRICKET FUNCTIONS ====================
    fun addLegalBall() {
        if (ballCounter == 5) {
            currentOvers = (currentOvers.toInt() + 1).toFloat()
            ballCounter = 0
        } else {
            ballCounter++
            currentOvers = currentOvers.toInt() + (ballCounter * 0.1f)
        }
    }

    fun saveState() {
        previousState = MatchState(
            innings = currentInnings,
            aRuns = teamARuns,
            aWickets = teamAWickets,
            bRuns = teamBRuns,
            bWickets = teamBWickets,
            overs = currentOvers,
            ballCount = ballCounter,
            target = targetRuns,
            history = ballHistory.toList()
        )
    }

    fun recordBall(symbol: String) {
        ballHistory = (ballHistory + symbol).takeLast(6)
    }

    fun undoLastAction() {
        previousState?.let { state ->
            currentInnings = state.innings
            teamARuns = state.aRuns
            teamAWickets = state.aWickets
            teamBRuns = state.bRuns
            teamBWickets = state.bWickets
            currentOvers = state.overs
            ballCounter = state.ballCount
            targetRuns = state.target
            ballHistory = state.history.toList()
            matchResult = null
            previousState = null
            isFreeHit = false
        }
    }

    fun handleCricketRuns(runs: Int, isWicket: Boolean = false, isExtra: Boolean = false) {
        if (matchResult != null) return
        saveState()

        if (isWicket && !isFreeHit) {
            if (currentInnings == 1) teamAWickets++ else teamBWickets++
            recordBall("W")
            if (!isExtra) addLegalBall()
        } else if (!isWicket) {
            if (currentInnings == 1) teamARuns += runs else teamBRuns += runs
            val symbol = when (runs) {
                0 -> "0"; 4 -> "4"; 6 -> "6"; else -> "$runs"
            }
            recordBall(symbol)
            if (!isExtra) addLegalBall()
        }

        if (isFreeHit) isFreeHit = false

        val currentWickets = if (currentInnings == 1) teamAWickets else teamBWickets
        if ((currentOvers >= oversLimit && ballCounter == 0) || currentWickets == 10) {
            if (currentInnings == 1) {
                currentInnings = 2
                targetRuns = teamARuns + 1
                currentOvers = 0f
                ballCounter = 0
            } else {
                matchResult = if (teamBRuns >= targetRuns)
                    "$teamBName won by ${10 - teamBWickets} wickets"
                else
                    "$teamAName won by ${targetRuns - teamBRuns - 1} runs"
            }
        }

        if (currentInnings == 2 && teamBRuns >= targetRuns && matchResult == null) {
            matchResult = "$teamBName won by ${10 - teamBWickets} wickets"
        }
    }

    // ==================== VOLLEYBALL FUNCTIONS ====================
    fun handleVolleyballPoint(teamScored: Int) {
        if (teamScored == 1) {
            teamAPoints++
            servingTeam = 1
        } else {
            teamBPoints++
            servingTeam = 2
        }

        if (teamAPoints >= 25 && teamAPoints - teamBPoints >= 2) {
            teamASets++
            teamAPoints = 0
            teamBPoints = 0
            servingTeam = 2
        } else if (teamBPoints >= 25 && teamBPoints - teamAPoints >= 2) {
            teamBSets++
            teamAPoints = 0
            teamBPoints = 0
            servingTeam = 1
        }

        if (teamASets == 2) {
            matchResult = "$teamAName won the match (${teamASets}-$teamBSets sets)"
        } else if (teamBSets == 2) {
            matchResult = "$teamBName won the match (${teamBSets}-$teamASets sets)"
        }
    }

    // ==================== KABADDI FUNCTIONS ====================
    fun handleRaid(points: Int) {
        if (raidingTeam == 1) {
            kabTeamAPoints += points
        } else {
            kabTeamBPoints += points
        }
        // Auto switch raiding team
        raidingTeam = if (raidingTeam == 1) 2 else 1
    }

    fun handleDefendingBonus() {
        // Defending team gets bonus point
        if (raidingTeam == 1) {
            kabTeamBPoints += 1
        } else {
            kabTeamAPoints += 1
        }
        // Auto switch raiding team
        raidingTeam = if (raidingTeam == 1) 2 else 1
    }

    fun handleTackle() {
        // Defending team gets 1 point for tackle
        if (raidingTeam == 1) {
            kabTeamBPoints += 1
        } else {
            kabTeamAPoints += 1
        }
        // Auto switch raiding team
        raidingTeam = if (raidingTeam == 1) 2 else 1
    }

    fun handleAllOut() {
        if (raidingTeam == 1) {
            kabTeamAPoints += 2
        } else {
            kabTeamBPoints += 2
        }
        raidingTeam = if (raidingTeam == 1) 2 else 1
    }

    fun declareKabaddiWinner() {
        matchResult = if (kabTeamAPoints > kabTeamBPoints) {
            "$teamAName won the match ($kabTeamAPoints-$kabTeamBPoints)"
        } else if (kabTeamBPoints > kabTeamAPoints) {
            "$teamBName won the match ($kabTeamBPoints-$kabTeamAPoints)"
        } else {
            "Match Tied!"
        }
    }

    // ==================== UI ====================
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = { onMatchEnd() }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "⚡ LIVE SCORE ⚡",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(sport, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Team Scores Display - Fixed layout to prevent overlap
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Team A Score Card
            Card(
                modifier = Modifier.weight(1f).padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        teamAName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    when (sport) {
                        "Cricket" -> {
                            Text(
                                "${teamARuns}/${teamAWickets}",
                                color = Color(0xFFFFEB3B),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Overs: ${currentOvers.toInt()}.${ballCounter}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                        "Volleyball" -> {
                            Text(
                                "$teamAPoints",
                                color = Color(0xFFFFEB3B),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Sets: $teamASets",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        "Kabaddi" -> {
                            Text(
                                "$kabTeamAPoints",
                                color = Color(0xFFFFEB3B),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Team B Score Card
            Card(
                modifier = Modifier.weight(1f).padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        teamBName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    when (sport) {
                        "Cricket" -> {
                            Text(
                                "${teamBRuns}/${teamBWickets}",
                                color = Color(0xFFFFEB3B),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (currentInnings == 2 && matchResult == null) {
                                Text(
                                    "Need ${targetRuns - teamBRuns}",
                                    color = Color(0xFFFF9800),
                                    fontSize = 10.sp
                                )
                            }
                        }
                        "Volleyball" -> {
                            Text(
                                "$teamBPoints",
                                color = Color(0xFFFFEB3B),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Sets: $teamBSets",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        "Kabaddi" -> {
                            Text(
                                "$kabTeamBPoints",
                                color = Color(0xFFFFEB3B),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ==================== SPORT-SPECIFIC UI ====================
        when (sport) {
            "Cricket" -> {
                if (matchResult == null) {
                    Text(
                        "🏏 CRICKET SCORING 🏏",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Ball History
                    Text("Recent Balls", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val ballsToShow = ballHistory.takeLast(6)
                        repeat(6) { index ->
                            val ball = ballsToShow.getOrNull(index) ?: "●"
                            val ballColor = when {
                                ball == "W" -> Color.Red
                                ball == "4" || ball == "6" -> Color.Yellow
                                ball == "0" -> Color.Green
                                ball.toIntOrNull() in 1..3 -> Color.White
                                else -> Color.Gray
                            }
                            Box(
                                modifier = Modifier.size(32.dp).background(ballColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ball, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    // Cricket Buttons
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(onClick = { handleCricketRuns(0) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B))) { Text("0", fontSize = 12.sp) }
                        Button(onClick = { handleCricketRuns(1) }, modifier = Modifier.weight(1f)) { Text("1", fontSize = 12.sp) }
                        Button(onClick = { handleCricketRuns(2) }, modifier = Modifier.weight(1f)) { Text("2", fontSize = 12.sp) }
                        Button(onClick = { handleCricketRuns(3) }, modifier = Modifier.weight(1f)) { Text("3", fontSize = 12.sp) }
                        Button(onClick = { handleCricketRuns(4) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) { Text("4", fontSize = 12.sp) }
                        Button(onClick = { handleCricketRuns(6) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) { Text("6", fontSize = 12.sp) }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(onClick = { handleCricketRuns(0, true) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("W", fontSize = 12.sp) }
                        Button(onClick = { isFreeHit = true; showNoBallDialog = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) { Text("NB", fontSize = 12.sp) }
                        Button(onClick = { handleCricketRuns(1, false, true) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))) { Text("WD", fontSize = 12.sp) }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(onClick = { undoLastAction() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF607D8B))) { Text("↩️ Undo", fontSize = 12.sp) }
                        Button(onClick = { showEndMatchDialog = true }, modifier = Modifier.weight(2f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) { Text("🏁 End Match", fontSize = 12.sp) }
                    }
                } else {
                    Button(onClick = { showEndMatchDialog = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                        Text("🏆 Show Result")
                    }
                }
            }

            "Volleyball" -> {
                // Serving Team Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(teamAName, fontWeight = if (servingTeam == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                    Switch(
                        checked = servingTeam == 2,
                        onCheckedChange = { isChecked -> servingTeam = if (isChecked) 2 else 1 },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(teamBName, fontWeight = if (servingTeam == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                }
                Text("🏐 SERVING TEAM", fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))

                // Set Status
                Text(
                    "Sets: $teamASets - $teamBSets",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Points: $teamAPoints - $teamBPoints",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Target: 25 points (win by 2)",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                if (matchResult == null) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { handleVolleyballPoint(1) },
                            modifier = Modifier.weight(1f).height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("$teamAName\nWins Rally", fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                        Button(
                            onClick = { handleVolleyballPoint(2) },
                            modifier = Modifier.weight(1f).height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("$teamBName\nWins Rally", fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { showEndMatchDialog = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) { Text("🏁 End Match", fontSize = 14.sp) }
                } else {
                    Button(
                        onClick = { showEndMatchDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("🏆 Show Result") }
                }
            }

            "Kabaddi" -> {
                // Raiding Team Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(teamAName, fontWeight = if (raidingTeam == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                    Switch(
                        checked = raidingTeam == 2,
                        onCheckedChange = { isChecked -> raidingTeam = if (isChecked) 2 else 1 },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(teamBName, fontWeight = if (raidingTeam == 2) FontWeight.Bold else FontWeight.Normal, fontSize = 14.sp)
                }
                Text("🤼 RAIDING TEAM", fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(4.dp))

                // Timer and Target Score Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF263238))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Timer
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("⏱️ TIME", fontSize = 9.sp, color = Color.Gray)
                            Text(formatTime(matchTimeSeconds), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Row {
                                Button(onClick = { isTimerRunning = !isTimerRunning }, modifier = Modifier.size(40.dp, 25.dp)) {
                                    Text(if (isTimerRunning) "⏸️" else "▶️", fontSize = 10.sp)
                                }
                                Button(onClick = { matchTimeSeconds = 0 }, modifier = Modifier.size(40.dp, 25.dp)) {
                                    Text("🔄", fontSize = 10.sp)
                                }
                            }
                        }

                        // Target Score Display
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text("🎯 TARGET", fontSize = 9.sp, color = Color.Gray)
                            Text("$targetScore", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFEB3B))
                            Button(onClick = { showTargetDialog = true }, modifier = Modifier.size(40.dp, 25.dp)) {
                                Text("Set", fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                if (matchResult == null) {
                    // Check if target score reached
                    val targetInt = targetScore.toIntOrNull() ?: 30
                    if (kabTeamAPoints >= targetInt || kabTeamBPoints >= targetInt) {
                        Text(
                            "🎯 TARGET REACHED! Declare Winner",
                            color = Color(0xFFFF5722),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // RAIDING TEAM ACTIONS
                    Text("⚔️ RAIDING TEAM ACTIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { handleRaid(1) },
                            modifier = Modifier.weight(1f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Touch", fontSize = 10.sp); Text("+1", fontWeight = FontWeight.Bold) } }
                        Button(
                            onClick = { handleRaid(2) },
                            modifier = Modifier.weight(1f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Bonus", fontSize = 10.sp); Text("+2", fontWeight = FontWeight.Bold) } }
                        Button(
                            onClick = { handleRaid(3) },
                            modifier = Modifier.weight(1f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                        ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Super", fontSize = 10.sp); Text("+3", fontWeight = FontWeight.Bold) } }
                        Button(
                            onClick = { handleAllOut() },
                            modifier = Modifier.weight(1f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("All Out", fontSize = 10.sp); Text("+2", fontWeight = FontWeight.Bold) } }
                    }

                    Spacer(Modifier.height(4.dp))

                    // DEFENDING TEAM ACTIONS
                    Text("🛡️ DEFENDING TEAM ACTIONS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { handleDefendingBonus() },
                            modifier = Modifier.weight(1f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Bonus", fontSize = 10.sp); Text("+1", fontWeight = FontWeight.Bold) } }
                        Button(
                            onClick = { handleTackle() },
                            modifier = Modifier.weight(1f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Tackle", fontSize = 10.sp); Text("+1", fontWeight = FontWeight.Bold) } }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { declareKabaddiWinner() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                        ) { Text("🏆 Declare Winner", fontSize = 12.sp) }
                        Button(
                            onClick = { showEndMatchDialog = true },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) { Text("🏁 End Match", fontSize = 12.sp) }
                    }
                } else {
                    Button(
                        onClick = { showEndMatchDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("🏆 Show Result") }
                }
            }
        }
    }

    // ==================== DIALOGS ====================
    if (showNoBallDialog) {
        AlertDialog(
            onDismissRequest = { showNoBallDialog = false; noBallRuns = "" },
            title = { Text("No Ball - Extra Runs") },
            text = {
                Column {
                    Text("Free Hit next ball", color = Color(0xFFFF5722), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noBallRuns,
                        onValueChange = { if (it.all { it.isDigit() }) noBallRuns = it },
                        label = { Text("Runs off bat (0-6)") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val runs = noBallRuns.toIntOrNull()?.takeIf { it in 0..6 } ?: 0
                    handleCricketRuns(runs, false, true)
                    showNoBallDialog = false
                    noBallRuns = ""
                }) { Text("Add") }
            },
            dismissButton = { Button(onClick = { showNoBallDialog = false; noBallRuns = "" }) { Text("Cancel") } }
        )
    }

    if (showTargetDialog) {
        var tempTarget by remember { mutableStateOf(targetScore) }
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("Set Target Score") },
            text = {
                OutlinedTextField(
                    value = tempTarget,
                    onValueChange = { if (it.all { it.isDigit() }) tempTarget = it },
                    label = { Text("Target Score (e.g., 30)") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (tempTarget.isNotBlank()) targetScore = tempTarget
                    showTargetDialog = false
                }) { Text("Set") }
            },
            dismissButton = { Button(onClick = { showTargetDialog = false }) { Text("Cancel") } }
        )
    }

    if (showEndMatchDialog) {
        AlertDialog(
            onDismissRequest = { showEndMatchDialog = false },
            title = { Text("🏆 Match Result 🏆") },
            text = {
                Column {
                    if (matchResult != null) {
                        Text(matchResult!!, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4CAF50))
                    } else {
                        when (sport) {
                            "Cricket" -> {
                                Text("Final Score:", fontSize = 14.sp)
                                Text("$teamAName: $teamARuns/$teamAWickets", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("$teamBName: $teamBRuns/$teamBWickets", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("WINNER: ${if (teamARuns > teamBRuns) teamAName else if (teamBRuns > teamARuns) teamBName else "Tie"}", color = Color(0xFF4CAF50), fontSize = 16.sp)
                            }
                            "Volleyball" -> {
                                Text("🏐 MATCH RESULT 🏐", fontSize = 14.sp)
                                Text("$teamAName: $teamASets sets", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("$teamBName: $teamBSets sets", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("WINNER: ${if (teamASets > teamBSets) teamAName else teamBName}", color = Color(0xFF4CAF50), fontSize = 16.sp)
                            }
                            "Kabaddi" -> {
                                Text("🤼 MATCH RESULT 🤼", fontSize = 14.sp)
                                Text("$teamAName: $kabTeamAPoints points", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("$teamBName: $kabTeamBPoints points", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("WINNER: ${if (kabTeamAPoints > kabTeamBPoints) teamAName else teamBName}", color = Color(0xFF4CAF50), fontSize = 16.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { showEndMatchDialog = false; onMatchEnd() }) { Text("OK") } },
            dismissButton = { Button(onClick = { showEndMatchDialog = false }) { Text("Cancel") } }
        )
    }
}