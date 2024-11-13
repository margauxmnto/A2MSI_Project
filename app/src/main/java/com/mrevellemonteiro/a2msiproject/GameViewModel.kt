package com.mrevellemonteiro.a2msiproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.media.MediaPlayer
import android.content.Context
import com.mrevellemonteiro.a2msiproject.model.Track

class GameViewModel : ViewModel() {
    private val deezerRepository = DeezerRepository()
    private var currentTrack: Track? = null

    fun playNextSong() {
        viewModelScope.launch {
            try {
                val tracks = deezerRepository.searchTracks("pop") // ou toute autre requête de recherche
                if (tracks.isNotEmpty()) {
                    currentTrack = tracks.random()
                    // Ici, vous devriez implémenter la logique pour jouer l'extrait audio
                    // Par exemple, en utilisant un MediaPlayer
                }
            } catch (e: Exception) {
                // Gérer les erreurs, par exemple en les affichant à l'utilisateur
            }
        }
    }

    fun checkGuess(guess: String): Boolean {
        return guess.equals(currentTrack?.title, ignoreCase = true)
    }
}