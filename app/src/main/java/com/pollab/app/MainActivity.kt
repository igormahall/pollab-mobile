package com.pollab.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import com.example.app.presentation.AppNavigation
import com.example.app.ui.theme.EnqueteAppTheme
import com.pollab.app.SplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(2000)
                showSplash = false
            }

            EnqueteAppTheme {
                if (showSplash) {
                    SplashScreen()
                } else {
                    AppNavigation()
                }
            }
        }
    }
}
