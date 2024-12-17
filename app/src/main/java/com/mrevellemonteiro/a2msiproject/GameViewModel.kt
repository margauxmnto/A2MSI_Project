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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // État pour le titre et l'artiste devinés
    private val _titleGuess = MutableStateFlow("")
    val titleGuess: StateFlow<String> = _titleGuess

    private val _artistGuess = MutableStateFlow("")
    val artistGuess: StateFlow<String> = _artistGuess

    // État pour les options dans le niveau facile
    private val _options = MutableStateFlow<List<String>>(emptyList())
    val options: StateFlow<List<String>> = _options

    // État pour le score
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    // État pour le niveau de jeu
    private val _gameLevel = MutableStateFlow("hard")
    val gameLevel: StateFlow<String> = _gameLevel // Niveau par défaut

    // Piste actuelle
    private var currentTrack: Track? = null
    private var mediaPlayer: MediaPlayer? = null

    // Méthode pour définir le niveau de jeu
    fun setGameLevel(level: String) {
        _gameLevel.value = level
        playNextSong() // Joue une chanson au niveau sélectionné
    }

    // Méthodes pour mettre à jour les devinettes
    fun updateTitleGuess(guess: String) {
        _titleGuess.value = guess
    }

    fun updateArtistGuess(guess: String) {
        _artistGuess.value = guess
    }

    // Réinitialiser les devinettes
    fun resetGuesses() {
        _titleGuess.value = ""
        _artistGuess.value = ""
    }

    // Vérifier les devinettes pour le niveau difficile
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
        _score.value += pointsEarned // Mettre à jour le score total
        return pointsEarned
    }

    fun resetGame() {
        _score.value = 0
        _gameLevel.value = ""
        resetGuesses()
        // Réinitialisez d'autres états si nécessaire
    }

    // Vérifier les devinettes pour le niveau facile
    fun checkEasyGuess(selectedOption: String): Boolean {
        val isCorrect = selectedOption == currentTrack?.title
        if (isCorrect) {
            _score.value += 1 // Augmenter le score de 1 point
        }
        return isCorrect
    }

    // Jouer la prochaine chanson et préparer les options si nécessaire
    fun playNextSong() {
        viewModelScope.launch {
            try {
                val tracks = deezerRepository.searchTracks("e") // Remplacez par votre logique de recherche de pistes
                if (tracks.isNotEmpty()) {
                    currentTrack = tracks.random()
                    if (_gameLevel.value == "easy") {
                        val correctTitle = currentTrack?.title ?: ""
                        val optionsList = mutableListOf(correctTitle)

                        // Ajouter 3 titres incorrects
                        val incorrectTracks =
                            tracks.filter { it.title != correctTitle }.shuffled().take(3)
                        optionsList.addAll(incorrectTracks.map { it.title }) // Ajouter les titres incorrects

                        optionsList.shuffle() // Mélanger les options
                        _options.value = optionsList // Mettre à jour les options affichées à l'utilisateur
                    } else {
                        // Logique pour le niveau difficile (ne rien faire ici)
                        _options.value = emptyList()
                    }
                    _previewUrl.value = currentTrack?.preview ?: ""
                } else {
                    // Gérer le cas où aucune piste n'est trouvée
                    _errorMessage.value = "Aucune piste trouvée"
                }
            } catch (e: Exception) {
                // Gérer les erreurs potentielles lors de la recherche de pistes
                _errorMessage.value = "Erreur: ${e.message}"
            }
        }
    }
    fun playPreview() {
        _previewUrl.value?.let { url ->
            mediaPlayer?.release() // Libère les ressources du lecteur précédent
            mediaPlayer = MediaPlayer().apply {
                try {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(url) // Définit la source audio avec l'URL d'extrait
                    prepareAsync() // Prépare le lecteur en arrière-plan (asynchrone)
                    setOnPreparedListener { start() } // Joue l'extrait une fois prêt

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
        mediaPlayer?.release() // Libère les ressources du MediaPlayer lors de la destruction du ViewModel
    }
}
