package com.example.gramakalyanasports.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramakalyanasports.data.FirebaseRepository
import com.example.gramakalyanasports.models.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _currentMatch = MutableStateFlow<Match?>(null)
    val currentMatch: StateFlow<Match?> = _currentMatch.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val selectedSport: SportType = SportType.KABADDI,
        val teamAName: String = "",
        val teamBName: String = "",
        val tournamentName: String = "",
        val liveMatches: List<Match> = emptyList()
    )

    // Create new match
    fun createMatch() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val match = Match(
                tournamentName = _uiState.value.tournamentName,
                sportType = _uiState.value.selectedSport,
                teamA = Team(teamName = _uiState.value.teamAName),
                teamB = Team(teamName = _uiState.value.teamBName),
                isLive = true
            )
            val matchId = repository.saveMatch(match)
            _currentMatch.value = match.copy(matchId = matchId)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    // Update score (called from Scorer screen)
    fun updateScore(teamAScore: Int, teamBScore: Int, wickets: Int = 0, overs: Float = 0f) {
        viewModelScope.launch {
            _currentMatch.value?.let { match ->
                repository.updateScore(match.matchId, teamAScore, teamBScore, wickets, overs)
                _currentMatch.value = match.copy(
                    currentScore = Score(teamAScore, teamBScore, wickets, overs)
                )
            }
        }
    }

    // Subscribe to live match
    fun subscribeToLiveMatch(matchId: String) {
        viewModelScope.launch {
            repository.getLiveMatch(matchId).collect { match ->
                _currentMatch.value = match
            }
        }
    }

    fun updateUiState(update: (UiState) -> UiState) {
        _uiState.update { update(it) }
    }
}