package com.mrevellemonteiro.a2msiproject

import android.os.Bundle
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
    var titleGuess by remember { mutableStateOf("") }
    var artistGuess by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
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
            onValueChange = { titleGuess = it },
            label = { Text("Entrez le titre de la chanson") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = artistGuess,
            onValueChange = { artistGuess = it },
            label = { Text("Entrez le nom de l'artiste") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (viewModel.checkGuess(titleGuess, artistGuess)) {
                score++
                titleGuess = ""
                artistGuess = ""
            }
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
