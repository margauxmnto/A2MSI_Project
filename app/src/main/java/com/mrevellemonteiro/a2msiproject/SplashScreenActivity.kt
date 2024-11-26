package com.mrevellemonteiro.a2msiproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.mrevellemonteiro.a2msiproject.ui.theme.A2MSIProjectTheme
import kotlinx.coroutines.delay

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            A2MSIProjectTheme {
                SplashScreen {
                    // Naviguer vers MainActivity après le splash screen
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Terminer SplashScreenActivity
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Utilisez LaunchedEffect pour effectuer une action après un délai
    LaunchedEffect(Unit) {
        delay(5000) // Attendez 5 secondes
        onTimeout() // Appelez la fonction pour naviguer vers MainActivity
    }

    // Chargez l'animation Lottie à partir du fichier JSON dans les ressources raw
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_logo))
    val progress = animateLottieCompositionAsState(compositionResult.value)

    // Interface du Splash Screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Affichage de l'animation Lottie du logo
            LottieAnimation(compositionResult.value, progress.value, modifier = Modifier.size(200.dp))

            Spacer(modifier = Modifier.height(16.dp))
            BasicText(
                text = "Bienvenue dans l'application !",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            BasicText(
                text = "Chargement...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
