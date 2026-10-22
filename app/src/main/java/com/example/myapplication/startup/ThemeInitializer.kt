package com.example.myapplication.startup
import android.content.Context
import androidx.startup.Initializer
class ThemeInitializer : Initializer<ThemeManager> {
    override fun create(context: Context): ThemeManager {
        val themeManager = ThemeManager(context)
        configureTheme(context, themeManager)
        return themeManager
    }
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
    private fun configureTheme(context: Context, themeManager: ThemeManager) {
        android.util.Log.d("ThemeInitializer", "Theme manager configured for ${context.packageName}")
    }
}
class ThemeManager(private val context: Context) {
    fun getCurrentTheme(): String {
        return if (isDarkMode()) "dark" else "light"
    }
    fun isDarkMode(): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and 
            android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}