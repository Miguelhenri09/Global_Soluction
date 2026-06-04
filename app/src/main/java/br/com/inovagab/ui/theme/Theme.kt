package br.com.inovagab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NavyBlue = Color(0xFF0D2444)
val MediumBlue = Color(0xFF1565C0)
val LightBlue = Color(0xFF1E88E5)
val AccentBlue = Color(0xFF00B4D8)
val BackgroundLight = Color(0xFFF5F7FA)
val SurfaceWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF546E7A)
val ErrorRed = Color(0xFFD32F2F)
val SuccessGreen = Color(0xFF2E7D32)
val WarningOrange = Color(0xFFF57C00)

private val LightColorScheme = lightColorScheme(
    primary = MediumBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    secondary = AccentBlue,
    onSecondary = Color.White,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = Color.White,
)

@Composable
fun InovaGABTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
