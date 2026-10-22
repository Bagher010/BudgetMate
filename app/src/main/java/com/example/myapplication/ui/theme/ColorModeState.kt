package com.example.myapplication.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

enum class AppThemeMode(val label: String) {
    SYSTEM("Follow device"),
    LIGHT("Light"),
    DARK("Dark")
}

@Stable
class ColorModeState(context: Context) {
    private val preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    var themeMode by mutableStateOf(
        preferences.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
            ?.let { stored -> AppThemeMode.entries.firstOrNull { it.name == stored } }
            ?: AppThemeMode.SYSTEM
    )
        private set

    fun updateThemeMode(mode: AppThemeMode) {
        themeMode = mode
        preferences.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
    }
}

@Composable
fun rememberColorModeState(): ColorModeState {
    val context = LocalContext.current
    return remember(context) { ColorModeState(context.applicationContext) }
}
