package com.example.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Paleta modo escuro (Dark Theme)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DD0E1),              // Azul-claro para elementos interativos
    background = Color(0xFF0F172A),           // Azul-marinho profundo para fundo
    surface = Color(0xFF1E293B),              // Cards e superfícies mais elevadas
    onPrimary = Color(0xFF0F172A),            // Texto sobre botões primários
    onBackground = Color(0xFFCBD5E1),         // Texto principal
    onSurface = Color(0xFFCBD5E1),            // Texto em superfícies
    onSurfaceVariant = Color(0xFF94A3B8)      // Subtítulos, dicas, ícones
)

// Paleta modo claro (Light Theme)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00658E),              // Azul principal
    background = Color(0xFFF8FAFC),           // Fundo geral muito claro
    surface = Color(0xFFE2E8F0),              // Cards, botões, caixas
    onPrimary = Color.White,                        // Texto sobre botão primário
    onBackground = Color(0xFF1E293B),         // Texto principal (quase preto)
    onSurface = Color(0xFF1E293B),            // Texto em cards, botões
    onSurfaceVariant = Color(0xFF475569)      // Texto secundário
)

data class ExtendedColors(
    val cardAberta: Color,
    val cardFechada: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        cardAberta = CardAbertaLight,
        cardFechada = CardFechadaLight
    )
}


@Composable
fun EnqueteAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) {
        ExtendedColors(CardAbertaDark, CardFechadaDark)
    } else {
        ExtendedColors(CardAbertaLight, CardFechadaLight)
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current