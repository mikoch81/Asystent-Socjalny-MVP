package pl.mikoch.asystentsocjalny.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Mobile Social Shield brand: granat MOPS + ciepły akcent + alarm czerwony
private val BrandPrimary = Color(0xFF1E4D8C)        // granat
private val BrandPrimaryDark = Color(0xFF7FB0E8)
private val BrandSecondary = Color(0xFF5C7A99)
private val BrandSecondaryDark = Color(0xFFB6CCE0)
private val BrandTertiary = Color(0xFFB58A00)       // ciepły mosiądz (uzupełnij profil)
private val BrandTertiaryDark = Color(0xFFE8C76A)
private val BrandError = Color(0xFFB3261E)          // alarmowy czerwony

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7E4F6),
    onPrimaryContainer = Color(0xFF0A1F3F),
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE6F0),
    onSecondaryContainer = Color(0xFF1A2A3A),
    tertiary = BrandTertiary,
    tertiaryContainer = Color(0xFFFFE9A8),
    onTertiaryContainer = Color(0xFF3A2B00),
    error = BrandError,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryDark,
    onPrimary = Color(0xFF002B5E),
    primaryContainer = Color(0xFF14365E),
    onPrimaryContainer = Color(0xFFD7E4F6),
    secondary = BrandSecondaryDark,
    secondaryContainer = Color(0xFF35506B),
    tertiary = BrandTertiaryDark,
    tertiaryContainer = Color(0xFF5A4500),
    onTertiaryContainer = Color(0xFFFFE9A8),
    error = Color(0xFFF2B8B5),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC)
)

@Composable
fun AsystentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
