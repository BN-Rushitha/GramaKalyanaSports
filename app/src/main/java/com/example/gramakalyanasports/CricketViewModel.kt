package com.example.gramakalyanasports

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class CricketViewModel : ViewModel() {
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

    fun recordLegalBall(runs: Int, isWicket: Boolean = false) {
        val totalBallsAllowed = totalOversLimit * 6

        // Prevent adding more balls if match is over
        if (ballsBowled >= totalBallsAllowed) return

        saveSnapshot()

        if (!isFreeHit || !isWicket) {
            if (isWicket) totalWickets += 1
        }

        totalRuns += runs
        ballsBowled += 1
        currentOverBalls.add(if (isWicket && !isFreeHit) "W" else runs.toString())
        isFreeHit = false

        // 2. Fix: Reset display/Match end logic
        if (ballsBowled >= totalBallsAllowed) {
            matchStarted = false
        } else if (ballsBowled % 6 == 0) {
            // Reset the "row of 6 balls" display after each over
            currentOverBalls.clear()
        }
    }

    fun openExtraPrompt(type: String) {
        pendingExtraType = type
        showExtraDialog = true
    }

    fun resolveExtra(extraRuns: Int) {
        val totalBallsAllowed = totalOversLimit * 6

        // Block extras if the over/match is already finished
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
        }
    }

    private fun saveSnapshot() {
        matchHistory.add(MatchSnapshot(totalRuns, totalWickets, ballsBowled, currentOverBalls.toList(), isFreeHit))
    }
}

data class MatchSnapshot(val runs: Int, val wickets: Int, val balls: Int, val overList: List<String>, val freeHit: Boolean)