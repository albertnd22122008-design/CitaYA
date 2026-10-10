package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CitaYaDarkColorScheme = darkColorScheme(
    primary = SunsetOrange,
    onPrimary = Color.White,
    primaryContainer = SunsetOrangeLight,
    onPrimaryContainer = SunsetOrange,
    secondary = CobaltBlue,
    onSecondary = Color.White,
    secondaryContainer = CobaltBlueLight,
    onSecondaryContainer = CobaltBlue,
    tertiary = GoldVip,
    onTertiary = Color.Black,
    background = ObsidianDeep,
    onBackground = TextLight,
    surface = ObsidianSurface,
    onSurface = TextLight,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextMuted,
    outline = BorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CitaYaDarkColorScheme,
        typography = Typography,
        content = content
    )
}
