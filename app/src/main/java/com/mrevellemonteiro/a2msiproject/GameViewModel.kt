package com.mrevellemonteiro.a2msiproject

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrevellemonteiro.a2msiproject.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val deezerRepository = DeezerRepository()
    private val playlistId = 10792003862L // ID de la playlist

    private val _previewUrl = MutableStateFlow<String?>(null)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _titleGuess = MutableStateFlow("")
    val titleGuess: StateFlow<String> = _titleGuess

    private val _artistGuess = MutableStateFlow("")
    val artistGuess: StateFlow<String> = _artistGuess

    private val _options = MutableStateFlow<List<String>>(emptyList())
    val options: StateFlow<List<String>> = _options

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _gameLevel = MutableStateFlow("")
    val gameLevel: StateFlow<String> = _gameLevel

    private var currentTrack: Track? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playlistTracks: List<Track> = emptyList()

    fun setGameLevel(level: String) {
        _gameLevel.value = level
        loadPlaylistTracks()
    }

    private fun loadPlaylistTracks() {
        viewModelScope.launch {
            try {
                playlistTracks = deezerRepository.getPlaylistTracks(playlistId)
                if (playlistTracks.isNotEmpty()) {
                    playNextSong()
                } else {
                    _errorMessage.value = "Aucune piste trouvée dans la playlist"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur lors du chargement de la playlist : ${e.message}"
            }
        }
    }

    fun updateTitleGuess(guess: String) {
        _titleGuess.value = guess
    }

    fun updateArtistGuess(guess: String) {
        _artistGuess.value = guess
    }

    fun resetGuesses() {
        _titleGuess.value = ""
        _artistGuess.value = ""
    }

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
        if (pointsEarned > 0) {
            playNextSong()
        }
        return pointsEarned
    }

    fun resetGame() {
        _score.value = 0
        _gameLevel.value = ""
        resetGuesses()
        playlistTracks = emptyList()
    }

    fun checkEasyGuess(selectedOption: String): Boolean {
        val isCorrect = selectedOption == currentTrack?.title
        if (isCorrect) {
            _score.value += 1
            playNextSong()
        }
        return isCorrect
    }

    fun playNextSong() {
        if (playlistTracks.isNotEmpty()) {
            currentTrack = playlistTracks.random()
            _previewUrl.value = currentTrack?.preview ?: ""

            if (_gameLevel.value == "easy") {
                val correctTitle = currentTrack?.title ?: ""
                val optionsList = mutableListOf(correctTitle)

                val incorrectTracks = playlistTracks.filter { it.title != correctTitle }.shuffled().take(3)
                optionsList.addAll(incorrectTracks.map { it.title })
                optionsList.shuffle()
                _options.value = optionsList
            } else {
                _options.value = emptyList()
            }
        } else {
            _errorMessage.value = "Aucune piste disponible dans la playlist"
        }
    }

    fun playPreview() {
        _previewUrl.value?.let { url ->
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                try {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener { start() }

                    setOnErrorListener { _, what, extra ->
                        println("Erreur MediaPlayer : what=$what, extra=$extra")
                        true
                    }
                } catch (e: Exception) {
                    println("Erreur lors de la lecture : ${e.message}")
                }
            }
        } ?: run {
            println("Aucune URL disponible pour jouer l'extrait.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
