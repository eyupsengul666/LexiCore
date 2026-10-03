package com.dunyadanuzak.lexicore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EPSL_Yellow_Dark,
    secondary = Brand_Red_Absolute,
    tertiary = EPSL_Cyan_Accent,
    background = Dark_Bg,
    surface = Dark_Surface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Text_Secondary_Dark,
    surfaceVariant = Dark_Surface,
    outline = Dark_Border,
    outlineVariant = Dark_Border
)

private val LightColorScheme = lightColorScheme(
    primary = EPSL_Yellow_Light,
    secondary = Brand_Red_Absolute,
    tertiary = EPSL_Cyan_Light,
    background = Light_Bg,
    surface = Light_Surface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1C1E),
    onSurface = Color(0xFF1C1C1E),
    onSurfaceVariant = Text_Secondary_Light,
    surfaceVariant = Light_Surface,
    outline = Light_Border,
    outlineVariant = Light_Border
)

@Composable
fun TürkçeKelimeBulucuTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
