package com.example.authapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// Tema rosinha pastel para SignUp
private val PastelPinkColorScheme = lightColorScheme(
    primary = PastelPink,
    onPrimary = Color.White,
    primaryContainer = PastelPinkLight,
    onPrimaryContainer = PastelPinkDark,
    secondary = PastelRoseSecondary,
    onSecondary = Color.White,
    secondaryContainer = PastelRose,
    onSecondaryContainer = PastelPinkDark,
    tertiary = Pink80,
    onTertiary = Color.White,
    background = PastelBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = PastelSurface,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = PastelRose,
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFF4EFF4),
    inverseSurface = Color(0xFF313033),
    inversePrimary = Purple80,
    surfaceTint = PastelPink,
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000)
)

@Composable
fun AuthAppTheme(
    dynamicColor: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun PastelPinkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PastelPinkColorScheme,
        typography = Typography,
        content = content
    )
}