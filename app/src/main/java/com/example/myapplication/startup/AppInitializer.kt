package com.example.myapplication.startup
import android.content.Context
import androidx.startup.Initializer
import com.example.myapplication.data.repository.FinanceRepository
class AppInitializer : Initializer<AppComponents> {
    override fun create(context: Context): AppComponents {
        val repository = androidx.startup.AppInitializer.getInstance(context)
            .initializeComponent(DatabaseInitializer::class.java)
        return AppComponents(
            repository = repository,
            themeManager = ThemeManager(context)
        )
    }
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(
            DatabaseInitializer::class.java,
            ThemeInitializer::class.java
        )
    }
}
data class AppComponents(
    val repository: FinanceRepository,
    val themeManager: ThemeManager
)
