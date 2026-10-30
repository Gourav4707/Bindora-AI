package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    onPrimary = Color(0xFF022C22),
    primaryContainer = PrimaryDarkTeal,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = AccentGreen,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = PrimaryDarkTeal,
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = WarningColor,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF451A03),
    onTertiaryContainer = WarningBackground,
    background = Color(0xFF0B1715),
    surface = Color(0xFF132724),
    surfaceVariant = Color(0xFF1D3B36),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF335C54),
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = SoftMint,
    onPrimaryContainer = PrimaryDarkTeal,
    secondary = PrimaryDarkTeal,
    onSecondary = Color.White,
    secondaryContainer = SoftTeal,
    onSecondaryContainer = PrimaryDarkTeal,
    tertiary = WarningColor,
    onTertiary = Color.White,
    tertiaryContainer = WarningBackground,
    onTertiaryContainer = WarningColor,
    background = PrimaryBackground,
    surface = SurfaceBackground,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    onSurfaceVariant = SecondaryText,
    outline = SubtleBorder,
    error = ErrorColor,
    errorContainer = ErrorBackground
)

@Composable
fun BindoraTheme(
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

// Compatibility wrapper
@Composable
fun StudentFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = BindoraTheme(darkTheme = darkTheme, content = content)
