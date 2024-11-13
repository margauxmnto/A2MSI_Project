package com.mrevellemonteiro.a2msiproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.content.Context
import com.mrevellemonteiro.a2msiproject.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {
    private val deezerRepository = DeezerRepository()
    private var currentTrack: Track? = null
    private lateinit var audioPlayer: AudioPlayer

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun initialize(context: Context) {
        audioPlayer = AudioPlayer(context)
    }

    fun playNextSong() {
        viewModelScope.launch {
            try {
                val tracks = deezerRepository.searchTracks("pop")
                if (tracks.isNotEmpty()) {
                    currentTrack = tracks.random()
                    currentTrack?.preview?.let { previewUrl ->
                        audioPlayer.playAudio(previewUrl)
                    }
                } else {
                    _errorMessage.value = "Aucune piste trouvée"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur: ${e.message}"
            }
        }
    }

    private fun playTrack(track: Track) {
        audioPlayer.reset()
        audioPlayer.playAudio(track.preview)

        // Arrêter après 30 secondes
        viewModelScope.launch {
            kotlinx.coroutines.delay(30000)
            audioPlayer.pause()
        }
    }

    fun checkGuess(guess: String): Boolean {
        return guess.equals(currentTrack?.title, ignoreCase = true)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}