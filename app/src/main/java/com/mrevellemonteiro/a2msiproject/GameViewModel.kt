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

    private val _previewUrl = MutableStateFlow<String?>(null)
    val previewUrl: StateFlow<String?> = _previewUrl

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

    private val _gameLevel = MutableStateFlow("hard")
    val gameLevel: StateFlow<String> = _gameLevel

    private var currentTrack: Track? = null
    private var mediaPlayer: MediaPlayer? = null

    fun setGameLevel(level: String) {
        _gameLevel.value = level
        playNextSong()
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
            if (titleGuess.equals(track.title, ignoreCase = true)) pointsEarned++
            if (artistGuess.equals(track.artist.name, ignoreCase = true)) pointsEarned++
        }
        _score.value += pointsEarned
        return pointsEarned
    }

    fun resetGame() {
        _score.value = 0
        _gameLevel.value = ""
        resetGuesses()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun checkEasyGuess(selectedOption: String): Boolean {
        val isCorrect = selectedOption == currentTrack?.title
        if (isCorrect) {
            _score.value += 1
        }
        return isCorrect
    }

    fun playNextSong() {
        viewModelScope.launch {
            try {
                val tracks = deezerRepository.searchTracks("e")
                if (tracks.isNotEmpty()) {
                    currentTrack = tracks.random()
                    if (_gameLevel.value == "easy") {
                        val correctTitle = currentTrack?.title ?: ""
                        val optionsList = mutableListOf(correctTitle)
                        val incorrectTracks =
                            tracks.filter { it.title != correctTitle }.shuffled().take(3)
                        optionsList.addAll(incorrectTracks.map { it.title })
                        optionsList.shuffle()
                        _options.value = optionsList
                    } else {
                        _options.value = emptyList()
                    }
                    _previewUrl.value = currentTrack?.preview ?: ""
                } else {
                    _errorMessage.value = "Aucune piste trouvée"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erreur : ${e.message}"
            }
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
                    println("Exception MediaPlayer : ${e.message}")
                }
            }
        } ?: run {
            println("Aucune URL disponible pour jouer l'extrait.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}