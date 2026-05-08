package com.example.gramakalyanasports

enum class SportType {
    KABADDI, VOLLEYBALL, CRICKET
}

data class Match(
    val matchId: String = "",
    val tournamentName: String = "",
    val sportType: SportType = SportType.KABADDI,
    val teamA: Team = Team(),
    val teamB: Team = Team(),
    val currentScore: Score = Score(),
    val isLive: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class Team(
    val teamId: String = "",
    val teamName: String = "",
    val players: List<Player> = emptyList(),
    val totalPoints: Int = 0
)

data class Player(
    val playerId: String = "",
    val playerName: String = "",
    val jerseyNumber: Int = 0,
    val careerPoints: Int = 0,
    val manOfTheMatchCount: Int = 0
)

data class Score(
    val teamAScore: Int = 0,
    val teamBScore: Int = 0,
    val wickets: Int = 0,  // For cricket
    val overs: Float = 0f,  // For cricket
    val lastUpdated: Long = System.currentTimeMillis()
)