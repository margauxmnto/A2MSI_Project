package com.mrevellemonteiro.a2msiproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.mrevellemonteiro.a2msiproject.ui.theme.A2MSIProjectTheme

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            A2MSIProjectTheme {
                SplashScreen {
                    // Naviguer vers MainActivity après le clic
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Terminer SplashScreenActivity
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Chargez l'animation Lottie à partir du fichier JSON dans les ressources raw
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_logo))
    val progress = animateLottieCompositionAsState(compositionResult.value)

    // Interface du Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() }, // Utilisez remember ici
                indication = androidx.compose.foundation.ripple.rememberRipple(
                    bounded = false, // Effet ripple non limité au composant
                    color = MaterialTheme.colorScheme.primary // Utilise la couleur primaire pour le ripple
                ),
                onClick = { onTimeout() } // Naviguer vers MainActivity au clic
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animation Lottie en arrière-plan
        LottieAnimation(
            composition = compositionResult.value,
            progress = progress.value,
            modifier = Modifier.fillMaxSize() // Occupe tout l'écran
        )
    }
}
