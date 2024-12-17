package com.mrevellemonteiro.a2msiproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mrevellemonteiro.a2msiproject.ui.theme.A2MSIProjectTheme


class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A2MSIProjectTheme {
                val currentLevel by gameViewModel.gameLevel.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentLevel.isEmpty()) {
                        HomeScreen (onLevelSelected = { level ->
                            gameViewModel.setGameLevel(level) })
                    } else {
                        GameScreen(viewModel = gameViewModel, onBackToLevels = { gameViewModel.resetGame() } )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onLevelSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Choisissez un niveau", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onLevelSelected("easy") }) {
            Text("Niveau Facile")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { onLevelSelected("hard") }) {
            Text("Niveau Difficile")
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel, onBackToLevels: () -> Unit) {
    val context = LocalContext.current
    val titleGuess by viewModel.titleGuess.collectAsState()
    val artistGuess by viewModel.artistGuess.collectAsState()
    val score by viewModel.score.collectAsState()
    val options by viewModel.options.collectAsState() // Options pour le niveau facile
    val currentLevel by viewModel.gameLevel.collectAsState() // Niveau de jeu actuel
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onBackToLevels,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("Retour aux niveaux")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { viewModel.playNextSong() }) {
                Text("Jouer l'extrait")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentLevel == "easy") {
                // Affichage des options pour le niveau facile
                Text(text = "Choisissez la bonne réponse :")
                options.forEach { option ->
                    Button(onClick = {
                        if (viewModel.checkEasyGuess(option)) {
                            Toast.makeText(
                                context,
                                "Bonne réponse, vous avez gagné 1 point !",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, "Mauvaise réponse.", Toast.LENGTH_SHORT).show()
                        }
                        viewModel.playNextSong() // Joue automatiquement la chanson suivante
                    }) {
                        Text(option)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                // Logique pour le niveau difficile avec les champs de texte
                TextField(
                    value = titleGuess,
                    onValueChange = { viewModel.updateTitleGuess(it) },
                    label = { Text("Entrez le titre de la chanson") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = artistGuess,
                    onValueChange = { viewModel.updateArtistGuess(it) },
                    label = { Text("Entrez le nom de l'artiste") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val pointsEarned = viewModel.checkGuess(titleGuess, artistGuess)
                    when (pointsEarned) {
                        0 -> Toast.makeText(
                            context,
                            "Désolé, aucune bonne réponse.",
                            Toast.LENGTH_SHORT
                        ).show()

                        1 -> Toast.makeText(
                            context,
                            "Bravo ! Vous avez gagné 1 point !",
                            Toast.LENGTH_SHORT
                        ).show()

                        2 -> Toast.makeText(
                            context,
                            "Excellent ! Vous avez gagné 2 points !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetGuesses() // Réinitialise les champs de saisie
                    viewModel.playNextSong() // Joue automatiquement la chanson suivante
                }) {
                    Text("Deviner")
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}