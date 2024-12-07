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
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.mrevellemonteiro.a2msiproject.ui.theme.A2MSIProjectTheme
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameViewModel.initialize(this)
        setContent {
            A2MSIProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(gameViewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val context = LocalContext.current
    val titleGuess by viewModel.titleGuess.collectAsState()
    val artistGuess by viewModel.artistGuess.collectAsState()
    val score by viewModel.score.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
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
                0 -> Toast.makeText(context, "Désolé, aucune bonne réponse.", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(context, "Bravo ! Vous avez gagné 1 point !", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(context, "Excellent ! Vous avez gagné 2 points !", Toast.LENGTH_SHORT).show()
            }
            viewModel.resetGuesses()
            viewModel.playNextSong()
        }) {
            Text("Deviner")
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
