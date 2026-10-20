package com.example.rackstracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OnMint = Color(0xFF08100C)
private val RacksColorScheme = darkColorScheme(
    primary = Mint,
    onPrimary = OnMint,
    primaryContainer = MintDark,
    onPrimaryContainer = Mint,
    secondary = Mint,
    onSecondary = OnMint,
    tertiary = MintDim,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = CardBorder,
    error = Color(0xFFF87171),
    onError = OnMint
)

@Composable
fun RacksTrackerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    @Suppress("UNUSED_EXPRESSION")
    isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = RacksColorScheme,
        typography = Typography,
        content = content
    )
}