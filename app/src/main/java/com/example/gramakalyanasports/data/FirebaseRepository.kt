package com.example.gramakalyanasports.data

import com.example.gramakalyanasports.models.Match
import com.example.gramakalyanasports.models.Player
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance().reference

    // Create or update match
    suspend fun saveMatch(match: Match): String {
        val matchId = if (match.matchId.isEmpty())
            database.child("matches").push().key ?: ""
        else match.matchId

        val matchWithId = match.copy(matchId = matchId)
        database.child("matches").child(matchId).setValue(matchWithId).await()
        return matchId
    }

    // Live score stream for Fan View
    fun getLiveMatch(matchId: String): Flow<Match?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val match = snapshot.getValue(Match::class.java)
                trySend(match)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child("matches").child(matchId).addValueEventListener(listener)
        awaitClose { database.child("matches").child(matchId).removeEventListener(listener) }
    }

    // Update score (called by Scorer)
    suspend fun updateScore(matchId: String, teamAScore: Int, teamBScore: Int, wickets: Int = 0, overs: Float = 0f) {
        val updates = mapOf(
            "currentScore/teamAScore" to teamAScore,
            "currentScore/teamBScore" to teamBScore,
            "currentScore/wickets" to wickets,
            "currentScore/overs" to overs,
            "currentScore/lastUpdated" to System.currentTimeMillis()
        )
        database.child("matches").child(matchId).updateChildren(updates).await()
    }

    // Update player career stats
    suspend fun updatePlayerStats(playerId: String, points: Int, isManOfMatch: Boolean = false) {
        database.child("players").child(playerId).runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val player = currentData.getValue(Player::class.java) ?: return Transaction.success(currentData)
                val updatedPlayer = player.copy(
                    careerPoints = player.careerPoints + points,
                    manOfTheMatchCount = if (isManOfMatch) player.manOfTheMatchCount + 1 else player.manOfTheMatchCount
                )
                currentData.value = updatedPlayer
                return Transaction.success(currentData)
            }
            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {}
        }).await()
    }

    // Get all matches for history
    suspend fun getAllMatches(): List<Match> {
        val snapshot = database.child("matches").get().await()
        return snapshot.children.mapNotNull { it.getValue(Match::class.java) }
    }
}