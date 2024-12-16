package com.mrevellemonteiro.a2msiproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrevellemonteiro.a2msiproject.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val deezerRepository = DeezerRepository()
    private var currentTrack: Track? = null
    private var gameLevel: String = "hard"

    // StateFlow pour gérer le score
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    // StateFlow pour gérer les messages d'erreur
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // StateFlow pour gérer les options de réponse (pour le niveau facile)
    private val _options = MutableStateFlow<List<String>>(emptyList())
    val options: StateFlow<List<String>> = _options

    // Méthode pour définir le niveau de jeu
    fun setGameLevel(level: String) {
        gameLevel = level
        resetGuesses() // Réinitialiser les entrées lorsque le niveau change
    }

    // Méthode pour jouer la prochaine chanson
    fun playNextSong() {
        viewModelScope.launch {
            try {
                val tracks = deezerRepository.searchTracks("pop")
                if (tracks.isNotEmpty()) {
                    currentTrack = tracks.random()
                    if (gameLevel == "easy") {
                        generateEasyOptions(tracks)
                    } else {
                        // Logique pour le niveau difficile (titre et artiste)
                        _options.value = emptyList() // Pas d'options à afficher en mode difficile
                    }
                } else {
                    _errorMessage.value = "Aucune piste trouvée"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur: ${e.message}"
            }
        }
    }

    // Génère des options de réponse pour le niveau facile
    private fun generateEasyOptions(tracks: List<Track>) {
        val correctAnswer = currentTrack?.title ?: ""
        val incorrectAnswers = tracks.filter { it.title != correctAnswer }.shuffled().take(3).map { it.title }
        val allOptions = (incorrectAnswers + correctAnswer).shuffled() // Mélanger les réponses
        _options.value = allOptions
    }

    // Méthode pour vérifier les réponses de l'utilisateur
    fun checkGuess(titleGuess: String, artistGuess: String): Int {
        var pointsEarned = 0
        currentTrack?.let { track ->
            if (titleGuess.equals(track.title, ignoreCase = true)) {
                pointsEarned++
            }
            if (artistGuess.equals(track.artist.name, ignoreCase = true)) {
                pointsEarned++
            }
        }
        _score.value += pointsEarned
        return pointsEarned
    }

    // Méthode pour réinitialiser les champs de saisie après une tentative
    fun resetGuesses() {
        // Réinitialiser les entrées utilisateur si nécessaire, par exemple :
        // _titleGuess.value = ""
        // _artistGuess.value = ""
        // Ou toute autre logique nécessaire pour réinitialiser l'état du jeu.
        _options.value = emptyList() // Réinitialiser les options également.
    }

    override fun onCleared() {
        super.onCleared()
        // Libération des ressources si nécessaire.
    }
}
