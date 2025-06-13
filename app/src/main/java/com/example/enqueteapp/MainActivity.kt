package com.example.enqueteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.enqueteapp.presentation.AppNavigation
import com.example.enqueteapp.ui.theme.EnqueteAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnqueteAppTheme {
                // Não precisamos de Surface ou Scaffold aqui.
                // Nosso AppNavigation cuidará de tudo.
                AppNavigation()
            }
        }
    }
}