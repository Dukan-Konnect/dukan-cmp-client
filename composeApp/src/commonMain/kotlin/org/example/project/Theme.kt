import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.example.project.AppTypography // Import typography from Typography.kt

val BluePrimary = Color(0xFF283891)
val BluePrimaryVariant = Color(0xFF4A5CAB)
val BlueSecondary = Color(0xFF4A6CF7)
val AccentYellow = Color(0xFFF5C443)
val Green = Color(0xFF52B46B)
val SurfaceGray = Color(0xFFF3F3F3)
val OnPrimary = Color.White
val ErrorRed = Color(0xFFF54343)
val OutlineGray = Color(0xFFEBEBEB)

val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = OnPrimary,
    primaryContainer = BluePrimaryVariant,
    secondary = BlueSecondary,
    onSecondary = OnPrimary,
    secondaryContainer = Color.White,
    background = Color.White,
    onBackground = Color(0xFF161616),
    surface = Color.White,
    onSurface = Color(0xFF161616),
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = Color(0xFF757575),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFFABABAB)
)

val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = OnPrimary,
    primaryContainer = BluePrimaryVariant,
    secondary = BlueSecondary,
    onSecondary = OnPrimary,
    secondaryContainer = Color(0xFF202030),
    background = Color(0xFF161616),
    onBackground = Color.White,
    surface = Color(0xFF161616),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF202030),
    onSurfaceVariant = Color(0xFFE0E0E0),
    error = ErrorRed,
    onError = Color.Black,
    outline = Color(0xFF757575)
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false, // Replace with isSystemInDarkTheme() if you want to detect system
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
