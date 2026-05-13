package com.example.gramakalyanasports

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

data class StoredMatch(
    val matchId: String = "",
    val sport: String = "",
    val tournamentName: String = "",
    val teamAName: String = "",
    val teamBName: String = "",
    val teamAScore: String = "",
    val teamBScore: String = "",
    val winner: String = "",
    val live: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

object FirebaseManager {
    private val database = FirebaseDatabase.getInstance().reference

    suspend fun saveMatch(match: StoredMatch): String {
        val matchId = database.child("matches").push().key ?: ""
        val matchWithId = match.copy(matchId = matchId)
        database.child("matches").child(matchId).setValue(matchWithId).await()
        return matchId
    }

    suspend fun updateMatch(match: StoredMatch) {
        database.child("matches").child(match.matchId).setValue(match).await()
    }

    suspend fun getAllMatches(): List<StoredMatch> {
        val snapshot = database.child("matches").get().await()
        return snapshot.children.mapNotNull { it.getValue(StoredMatch::class.java) }
            .sortedByDescending { it.timestamp }
    }

    suspend fun getLiveMatch(): StoredMatch? {
        val allMatches = getAllMatches()
        return allMatches.find { it.live }
    }
}