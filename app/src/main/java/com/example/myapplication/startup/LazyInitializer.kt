package com.example.myapplication.startup
import android.content.Context
import androidx.startup.AppInitializer
import androidx.startup.Initializer
class LazyInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        registerLazyComponents(context)
    }
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(
            DatabaseInitializer::class.java
        )
    }
    private fun registerLazyComponents(context: Context) {
        android.util.Log.d("LazyInitializer", "Lazy components registered for ${context.packageName}")
    }
}
object LazyComponentManager {
    fun initializeTheme(context: Context) {
        try {
            AppInitializer.getInstance(context)
                .initializeComponent(ThemeInitializer::class.java)
            android.util.Log.d("LazyComponentManager", "Theme initialized on-demand")
        } catch (e: Exception) {
            android.util.Log.e("LazyComponentManager", "Failed to initialize theme", e)
        }
    }
}
