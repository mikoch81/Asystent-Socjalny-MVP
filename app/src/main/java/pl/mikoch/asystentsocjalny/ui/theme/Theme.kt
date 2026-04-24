package pl.mikoch.asystentsocjalny.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import pl.mikoch.asystentsocjalny.core.model.TextScale

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

// Wysoki kontrast: tła czyste czarne/białe, kolorystyka mocno nasycona,
// kontrast tekstu znacznie zwiększony — pomaga w słabych warunkach świetlnych
// i przy zmęczeniu wzroku w terenie.
private val HighContrastLightColors = lightColorScheme(
    primary = Color(0xFF002B5E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8D0EE),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF1F3447),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC9D7E4),
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFF704E00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD970),
    onTertiaryContainer = Color.Black,
    error = Color(0xFF8C0000),
    onError = Color.White,
    errorContainer = Color(0xFFFFC9C5),
    onErrorContainer = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE6E6E6),
    onSurfaceVariant = Color.Black,
    outline = Color.Black
)

private val HighContrastDarkColors = darkColorScheme(
    primary = Color(0xFFCDE0F8),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF002B5E),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFD7E2EE),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF233649),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFFD970),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF402B00),
    onTertiaryContainer = Color.White,
    error = Color(0xFFFFC9C5),
    onError = Color.Black,
    errorContainer = Color(0xFF8C0000),
    onErrorContainer = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1A1A1A),
    onSurfaceVariant = Color.White,
    outline = Color.White
)

private fun TextUnit.scaleSafe(factor: Float): TextUnit =
    if (this.type == TextUnitType.Sp) (this.value * factor).sp else this

private fun TextStyle.scale(factor: Float): TextStyle = copy(
    fontSize = fontSize.scaleSafe(factor),
    lineHeight = lineHeight.scaleSafe(factor)
)

private fun Typography.scaled(factor: Float): Typography {
    if (factor == 1.0f) return this
    return Typography(
        displayLarge = displayLarge.scale(factor),
        displayMedium = displayMedium.scale(factor),
        displaySmall = displaySmall.scale(factor),
        headlineLarge = headlineLarge.scale(factor),
        headlineMedium = headlineMedium.scale(factor),
        headlineSmall = headlineSmall.scale(factor),
        titleLarge = titleLarge.scale(factor),
        titleMedium = titleMedium.scale(factor),
        titleSmall = titleSmall.scale(factor),
        bodyLarge = bodyLarge.scale(factor),
        bodyMedium = bodyMedium.scale(factor),
        bodySmall = bodySmall.scale(factor),
        labelLarge = labelLarge.scale(factor),
        labelMedium = labelMedium.scale(factor),
        labelSmall = labelSmall.scale(factor)
    )
}

@Composable
fun AsystentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    textScale: TextScale = TextScale.MEDIUM,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColors = when {
        highContrast && darkTheme -> HighContrastDarkColors
        highContrast -> HighContrastLightColors
        darkTheme -> DarkColors
        else -> LightColors
    }
    val baseTypography = Typography()
    MaterialTheme(
        colorScheme = baseColors,
        typography = baseTypography.scaled(textScale.multiplier),
        content = content
    )
}

