package com.mrevellemonteiro.a2msiproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
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
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_logov2))
    val progress = animateLottieCompositionAsState(compositionResult.value)

    // Interface du Splash Screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
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
