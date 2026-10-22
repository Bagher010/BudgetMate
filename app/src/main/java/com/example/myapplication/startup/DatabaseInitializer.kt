package com.example.myapplication.startup
import android.content.Context
import androidx.startup.Initializer
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.repository.FinanceRepository
import com.example.myapplication.data.RepositoryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class DatabaseInitializer : Initializer<FinanceRepository> {
    override fun create(context: Context): FinanceRepository {
        val database = FinanceDatabase.getDatabase(context)
        val repository = RepositoryFactory.createRepository(database)
        warmUpDatabaseInBackground(context, database)
        return repository
    }
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
    private fun warmUpDatabaseInBackground(context: Context, database: FinanceDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                FinanceDatabase.warmUpDatabase(context)
                android.util.Log.d("DatabaseInitializer", "Database warmed up in background for ${context.packageName}")
            } catch (e: Exception) {
                android.util.Log.e("DatabaseInitializer", "Error warming up database for ${context.packageName}", e)
            }
        }
    }
}