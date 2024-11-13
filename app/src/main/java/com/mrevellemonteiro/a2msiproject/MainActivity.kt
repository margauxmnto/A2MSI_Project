package com.mrevellemonteiro.a2msiproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mrevellemonteiro.a2msiproject.ui.theme.A2MSIProjectTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel = GameViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    var guess by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }

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
            value = guess,
            onValueChange = { guess = it },
            label = { Text("Entrez le titre de la chanson") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (viewModel.checkGuess(guess)) {
                score++
            }
            guess = ""
        }) {
            Text("Deviner")
        }
    }
}