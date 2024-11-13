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
    private lateinit var mediaPlayer: MediaPlayer

    fun initialize(context: Context) {
        mediaPlayer = MediaPlayer()
    }

    fun playNextSong() {
        viewModelScope.launch {
            val tracks = deezerRepository.searchTracks("pop") // Vous pouvez varier la recherche
            if (tracks.isNotEmpty()) {
                currentTrack = tracks.random()
                playTrack(currentTrack!!)
            }
        }
    }

    private fun playTrack(track: Track) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(track.preview)
        mediaPlayer.prepare()
        mediaPlayer.start()

        // Arrêter après 30 secondes
        viewModelScope.launch {
            kotlinx.coroutines.delay(30000)
            mediaPlayer.pause()
        }
    }

    fun checkGuess(guess: String): Boolean {
        return guess.equals(currentTrack?.title, ignoreCase = true)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
    }
}