package com.mrevellemonteiro.a2msiproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.media.MediaPlayer
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
    fun stopPlayback() {
        audioPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }

    fun checkGuess(guess: String): Boolean {
        return guess.equals(currentTrack?.title, ignoreCase = true)
    }
}