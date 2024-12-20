package com.mrevellemonteiro.a2msiproject

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mrevellemonteiro.a2msiproject.ui.theme.A2MSIProjectTheme

class MainActivity : ComponentActivity() {
    private val gameViewModel: GameViewModel by viewModels()

    override fun onStop() {
        super.onStop()
        gameViewModel.stopMusic()
    }

    override fun onStart() {
        super.onStart()
        gameViewModel.playPreview()
    }

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
                        HomeScreen(onLevelSelected = { level ->
                            gameViewModel.setGameLevel(level)
                        })
                    } else {
                        GameScreen(
                            viewModel = gameViewModel,
                            onBackToLevels = { gameViewModel.resetGame() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onLevelSelected: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fond),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.Niveau),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onLevelSelected("easy") }) {
                Text(stringResource(R.string.facile))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { onLevelSelected("hard") }) {
                Text(stringResource(R.string.difficile))
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel, onBackToLevels: () -> Unit) {
    val context = LocalContext.current
    val titleGuess by viewModel.titleGuess.collectAsState()
    val artistGuess by viewModel.artistGuess.collectAsState()
    val score by viewModel.score.collectAsState()
    val options by viewModel.options.collectAsState()
    val currentLevel by viewModel.gameLevel.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)

    var showDialog by remember { mutableStateOf(false) }
    var newPlaylistId by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.fond),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
        Button(
            onClick = onBackToLevels,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.Retour))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { showDialog = true }) {
                Text(stringResource(R.string.Playlist))
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = stringResource(R.string.entrer_l_id_de_la_playlist_deezer)) },
                    text = {
                        TextField(
                            value = newPlaylistId,
                            onValueChange = { newPlaylistId = it },
                            label = { Text(stringResource(R.string.id_de_playlist)) },
                            placeholder = { Text("10792003862") }
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            val id = newPlaylistId.toLongOrNull()
                            if (id != null) {
                                viewModel.updatePlaylistId(id)
                                Toast.makeText(
                                    context,
                                    "Playlist mise à jour avec l'ID : $id",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showDialog = false
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.veuillez_entrer_un_id_valide),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text(stringResource(R.string.confirmer))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text(stringResource(R.string.annuler))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .background(
                        color = Color.White,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Score: $score",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { viewModel.playPreview() }) {
                    Text(stringResource(R.string.jouer_l_extrait))
                }
                Button(onClick = { viewModel.playNextSong()
                viewModel.playPreview()}) {
                    Text(stringResource(R.string.changer_de_chanson))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentLevel == "easy") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .background(
                            color = Color.White,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.choisissez_la_bonne_r_ponse),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Boutons alignés en carré (2x2)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        options.take(2).forEach { option ->
                            Button(
                                onClick = {
                                    if (viewModel.checkEasyGuess(option)) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.un_point),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.mauvaise_r_ponse),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    viewModel.playNextSong()
                                    viewModel.playPreview()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(option)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        options.drop(2).forEach { option ->
                            Button(
                                onClick = {
                                    if (viewModel.checkEasyGuess(option)) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.un_point),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.mauvaise_r_ponse),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    viewModel.playNextSong()
                                    viewModel.playPreview()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(option)
                            }
                        }
                    }
                }
            } else {
                TextField(
                    value = titleGuess,
                    onValueChange = { viewModel.updateTitleGuess(it) },
                    label = { Text(stringResource(R.string.entrez_le_titre_de_la_chanson)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = artistGuess,
                    onValueChange = { viewModel.updateArtistGuess(it) },
                    label = { Text(stringResource(R.string.entrez_le_nom_de_l_artiste)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val pointsEarned = viewModel.checkGuess(titleGuess, artistGuess)
                    when (pointsEarned) {
                        0 -> Toast.makeText(
                            context,
                            context.getString(R.string.aucune_bonne_r_ponse),
                            Toast.LENGTH_SHORT
                        ).show()
                        1 -> Toast.makeText(
                            context,
                            context.getString(R.string.un_point),
                            Toast.LENGTH_SHORT
                        ).show()
                        2 -> Toast.makeText(
                            context,
                            context.getString(R.string.excellent_vous_avez_gagn_2_points),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetGuesses()
                }) {
                    Text(stringResource(R.string.confirmer))
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
