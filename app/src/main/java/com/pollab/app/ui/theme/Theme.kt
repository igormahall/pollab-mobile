package com.example.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color // <-- IMPORTAÇÃO ADICIONADA AQUI

private val DarkColorScheme = darkColorScheme(
    primary = GreenAccent,
    background = NavyBlue,
    surface = NavyBlue,
    onPrimary = NavyBlue,
    onBackground = LightSlate,
    onSurface = LightSlate,
    onSurfaceVariant = Slate
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00658E),
    background = Color(0xFFF0F4F8),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun EnqueteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}