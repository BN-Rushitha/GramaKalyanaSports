package com.example.gramakalyanasports

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CricketViewModel : ViewModel() {
    // 1. Firebase Initialization
    private val database = Firebase.database.reference
    private val matchRef = database.child("live_matches").child("current_cricket_match")

    var matchStarted by mutableStateOf(false)
    var teamA by mutableStateOf("Team A")
    var teamB by mutableStateOf("Team B")
    var totalOversLimit by mutableIntStateOf(5)

    var totalRuns by mutableIntStateOf(0)
    var totalWickets by mutableIntStateOf(0)
    var ballsBowled by mutableIntStateOf(0)
    var isFreeHit by mutableStateOf(false)

    private val matchHistory = mutableStateListOf<MatchSnapshot>()
    var currentOverBalls = mutableStateListOf<String>()

    var showExtraDialog by mutableStateOf(false)
    var pendingExtraType by mutableStateOf("")

    // 2. This function sends everything to Firebase
    private fun syncToFirebase() {
        val matchData = mapOf(
            "teamA" to teamA,
            "teamB" to teamB,
            "totalRuns" to totalRuns,
            "totalWickets" to totalWickets,
            "ballsBowled" to ballsBowled,
            "isLive" to matchStarted,
            "oversDisplay" to getOversDisplay(),
            "currentOver" to currentOverBalls.toList()
        )
        matchRef.setValue(matchData)
    }

    fun recordLegalBall(runs: Int, isWicket: Boolean = false) {
        val totalBallsAllowed = totalOversLimit * 6
        if (ballsBowled >= totalBallsAllowed) return

        saveSnapshot()
        if (!isFreeHit || !isWicket) {
            if (isWicket) totalWickets += 1
        }

        totalRuns += runs
        ballsBowled += 1
        currentOverBalls.add(if (isWicket && !isFreeHit) "W" else runs.toString())
        isFreeHit = false

        if (ballsBowled >= totalBallsAllowed) {
            matchStarted = false
        } else if (ballsBowled % 6 == 0) {
            currentOverBalls.clear()
        }

        syncToFirebase() // Sync after ball
    }

    fun resolveExtra(extraRuns: Int) {
        val totalBallsAllowed = totalOversLimit * 6
        if (ballsBowled >= totalBallsAllowed) {
            showExtraDialog = false
            return
        }

        val penalty = if (pendingExtraType == "Manual") 0 else 1
        totalRuns += (extraRuns + penalty)
        val ballLabel = if (pendingExtraType == "Manual") "$extraRuns" else "$pendingExtraType+$extraRuns"
        currentOverBalls.add(ballLabel)

        saveSnapshot()
        showExtraDialog = false
        syncToFirebase() // Sync after extra
    }

    // 3. Special Function for the END MATCH button
    fun finalizeMatch() {
        matchStarted = false
        syncToFirebase() // This tells the Viewers the match is over!
    }

    fun getOversDisplay(): String = "${ballsBowled / 6}.${ballsBowled % 6}"

    fun undoLastAction() {
        if (matchHistory.isNotEmpty()) {
            val last = matchHistory.removeAt(matchHistory.size - 1)
            totalRuns = last.runs
            totalWickets = last.wickets
            ballsBowled = last.balls
            currentOverBalls.clear()
            currentOverBalls.addAll(last.overList)
            isFreeHit = last.freeHit
            syncToFirebase()
        }
    }

    private fun saveSnapshot() {
        matchHistory.add(MatchSnapshot(totalRuns, totalWickets, ballsBowled, currentOverBalls.toList(), isFreeHit))
    }
}