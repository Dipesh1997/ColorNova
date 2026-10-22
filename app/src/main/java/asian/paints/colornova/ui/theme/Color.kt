package asian.paints.colornova.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val NovaCoral = Color(0xFFFF6B6B)
val NovaTeal = Color(0xFF4ECDC4)
val NovaViolet = Color(0xFFA18CD1)
val NovaAmber = Color(0xFFFFB84C)

val DarkBackground = Color(0xFF0D111D)
val DarkSurface = Color(0xFF161C2C)
val DarkSurfaceElevated = Color(0xFF1F273D)
val DarkBorder = Color(0xFF2B3652)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

data class AppColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentTeal: Color,
    val accentCoral: Color,
    val accentViolet: Color,
    val accentGold: Color
)

val DarkAppColors = AppColors(
    isDark = true,
    background = Color(0xFF0D111D),
    surface = Color(0xFF161C2C),
    surfaceElevated = Color(0xFF1F273D),
    border = Color(0xFF2B3652),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textTertiary = Color(0xFF64748B),
    accentTeal = Color(0xFF4ECDC4),
    accentCoral = Color(0xFFFF6B6B),
    accentViolet = Color(0xFFA18CD1),
    accentGold = Color(0xFFFFD54F)
)

val LightAppColors = AppColors(
    isDark = false,
    background = Color(0xFFF4F6F9),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFE9EEF5),
    border = Color(0xFFDCE2EC),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textTertiary = Color(0xFF8B9CB2),
    accentTeal = Color(0xFF00897B),
    accentCoral = Color(0xFFE11D48),
    accentViolet = Color(0xFF7C3AED),
    accentGold = Color(0xFFD97706)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }