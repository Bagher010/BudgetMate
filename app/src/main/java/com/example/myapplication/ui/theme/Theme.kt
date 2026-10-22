package com.example.myapplication.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
object TransactionColors {
    val IncomeGreen = Color(0xFF087F23)
    val ExpenseRed = Color(0xFFB71C1C)
    val IncomeDarkGreen = Color(0xFF4CAF50)
    val ExpenseDarkRed = Color(0xFFFF5252)
}
@Composable
@ReadOnlyComposable
fun incomeColor(): Color {
    return if (isSystemInDarkTheme()) {
        TransactionColors.IncomeDarkGreen
    } else {
        TransactionColors.IncomeGreen
    }
}
@Composable
@ReadOnlyComposable
fun expenseColor(): Color {
    return if (isSystemInDarkTheme()) {
        TransactionColors.ExpenseDarkRed
    } else {
        TransactionColors.ExpenseRed
    }
}
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E676), 
    secondary = Color(0xFF90CAF9), 
    tertiary = Color(0xFFCE93D8), 
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E), 
    surfaceVariant = Color(0xFF2C2C2C), 
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFFB0B0B0),
    primaryContainer = Color(0xFF1B5E20), 
    secondaryContainer = Color(0xFF1976D2), 
    tertiaryContainer = Color(0xFF7B1FA2), 
    error = Color(0xFFD32F2F), 
    onError = Color.White,
    errorContainer = Color(0xFFB71C1C), 
    onErrorContainer = Color.White
)
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00C853), 
    secondary = Color(0xFF1976D2), 
    tertiary = Color(0xFF7B1FA2), 
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF), 
    surfaceVariant = Color(0xFFF5F5F5), 
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF666666),
    primaryContainer = Color(0xFFC8E6C9), 
    secondaryContainer = Color(0xFFBBDEFB), 
    tertiaryContainer = Color(0xFFE1BEE7), 
    error = Color(0xFFD32F2F), 
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2), 
    onErrorContainer = Color(0xFFB71C1C),
    outline = Color(0xFFE0E0E0),
    outlineVariant = Color(0xFFF5F5F5)
)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}