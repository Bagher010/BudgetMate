package com.example.myapplication
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.startup.AppInitializer
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.repository.FinanceRepository
import com.example.myapplication.data.RepositoryFactory
import com.example.myapplication.notification.NotificationHelper
import com.example.myapplication.notification.NotificationScheduler
import com.example.myapplication.startup.AppComponents
import com.example.myapplication.startup.LazyComponentManager
import com.example.myapplication.utils.CategoryManager
import com.example.myapplication.ui.screens.PopularSubscriptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
class FinanceApplication : Application() {
    private var appComponents: AppComponents? = null
    val database by lazy { 
        FinanceDatabase.getDatabase(this) 
    }
    val repository by lazy { 
        appComponents?.repository ?: RepositoryFactory.createRepository(database)
    }
    override fun onCreate() {
        super.onCreate()
        initializeAppComponents()
        initializeNotifications()
        optimizeMemory()
        warmUpDatabase()
        preWarmSubscriptionTemplates()
        trackStartupPerformance()
    }
    private fun initializeNotifications() {
        NotificationHelper.createNotificationChannels(this)
        NotificationScheduler.initializeSchedules(this)
        Log.d("FinanceApplication", "Notifications initialized")
    }
    private fun preWarmSubscriptionTemplates() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val categoriesCount = PopularSubscriptions.categories.size
                val templatesCount = PopularSubscriptions.all.size
                Log.d("FinanceApplication", "Pre-warmed $templatesCount subscription templates in $categoriesCount categories")
            } catch (e: Exception) {
                Log.e("FinanceApplication", "Failed to pre-warm subscription templates", e)
            }
        }
    }
    private fun initializeAppComponents() {
        try {
            appComponents = AppInitializer.getInstance(this)
                .initializeComponent(com.example.myapplication.startup.AppInitializer::class.java)
            Log.d("FinanceApplication", "App components initialized via App Startup")
        } catch (e: Exception) {
            Log.e("FinanceApplication", "Failed to initialize app components", e)
            initializeFallback()
        }
    }
    private fun initializeFallback() {
        Log.w("FinanceApplication", "Using fallback initialization")
    }
    private fun optimizeMemory() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val runtime = Runtime.getRuntime()
                runtime.gc() 
            } catch (e: Exception) {
                Log.e("FinanceApplication", "Memory optimization failed", e)
            }
        }
    }
    private fun warmUpDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = database.openHelper.readableDatabase
                db.query("SELECT COUNT(*) FROM transactions").use { cursor ->
                    cursor.moveToFirst()
                }
                db.query("SELECT COUNT(*) FROM categories").use { cursor ->
                    cursor.moveToFirst()
                }
                Log.d("FinanceApplication", "Database aggressively warmed up for instant performance")
            } catch (e: Exception) {
                Log.e("FinanceApplication", "Database warm-up failed", e)
                e.printStackTrace()
            }
        }
    }
    private fun trackStartupPerformance() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val startTime = System.currentTimeMillis()
                val dbStartTime = System.currentTimeMillis()
                database.openHelper.readableDatabase
                val dbInitTime = System.currentTimeMillis() - dbStartTime
                val repoStartTime = System.currentTimeMillis()
                repository
                val repoInitTime = System.currentTimeMillis() - repoStartTime
                val totalStartupTime = System.currentTimeMillis() - startTime
                Log.d("PerformanceMonitor", "🚀 Startup Performance Metrics:")
                Log.d("PerformanceMonitor", "  📊 Database Init: ${dbInitTime}ms")
                Log.d("PerformanceMonitor", "  📊 Repository Init: ${repoInitTime}ms")
                Log.d("PerformanceMonitor", "  📊 Total Startup: ${totalStartupTime}ms")
                val memoryInfo = android.os.Debug.MemoryInfo()
                android.os.Debug.getMemoryInfo(memoryInfo)
                val memoryUsed = memoryInfo.totalPss / 1024 
                Log.d("PerformanceMonitor", "  📊 Memory Used: ${memoryUsed}MB")
                val performanceRating = when {
                    totalStartupTime < 100 -> "🏆 EXCELLENT"
                    totalStartupTime < 500 -> "🥇 FAST"
                    totalStartupTime < 1000 -> "🥈 GOOD"
                    else -> "🥉 SLOW"
                }
                Log.d("PerformanceMonitor", "  📊 Performance Rating: $performanceRating")
                if (totalStartupTime > 1000) {
                    Log.w("PerformanceMonitor", "⚠️ Slow startup detected: ${totalStartupTime}ms")
                } else {
                    Log.i("PerformanceMonitor", "✅ Fast startup achieved: ${totalStartupTime}ms")
                }
            } catch (e: Exception) {
                Log.e("PerformanceMonitor", "Error tracking startup performance", e)
            }
        }
    }
    fun initializeThemeOnDemand() {
        LazyComponentManager.initializeTheme(this)
    }
}
