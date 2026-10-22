package com.example.myapplication.data.database
import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
object DatabasePerformanceOptimizer {
    fun optimizeForStartup(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("PRAGMA synchronous=NORMAL")
            db.execSQL("PRAGMA cache_size=1000")
            db.execSQL("PRAGMA temp_store=MEMORY")
            db.execSQL("PRAGMA page_size=4096")
            db.execSQL("PRAGMA foreign_keys=ON")
            Log.d("DatabaseOptimizer", "Additional startup optimizations applied successfully")
        } catch (e: Exception) {
            Log.e("DatabaseOptimizer", "Error applying startup optimizations", e)
        }
    }
    fun createOptimizedIndexes(db: SupportSQLiteDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_category_date ON transactions(categoryId, date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_type_date ON transactions(type, date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_category_type ON transactions(categoryId, type)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_amount ON transactions(amount)")
                Log.d("DatabaseOptimizer", "Optimized indexes created successfully")
            } catch (e: Exception) {
                Log.e("DatabaseOptimizer", "Error creating optimized indexes", e)
            }
        }
    }
    fun warmUpDatabase(context: Context, database: RoomDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                database.openHelper.readableDatabase
                preCompileCommonQueries(database.openHelper.readableDatabase)
                Log.d("DatabaseOptimizer", "Database warmed up successfully")
            } catch (e: Exception) {
                Log.e("DatabaseOptimizer", "Error warming up database", e)
            }
        }
    }
    private fun preCompileCommonQueries(db: SupportSQLiteDatabase) {
        try {
            val commonQueries = listOf(
                "SELECT * FROM transactions ORDER BY date DESC LIMIT ?",
                "SELECT * FROM categories WHERE type = ?",
                "SELECT * FROM transactions WHERE categoryId = ? ORDER BY date DESC",
                "SELECT SUM(amount) FROM transactions WHERE type = ? AND date >= ?"
            )
            commonQueries.forEach { query ->
                try {
                    val statement = db.compileStatement(query)
                    statement.close()
                } catch (e: Exception) {
                    Log.w("DatabaseOptimizer", "Could not pre-compile query: $query", e)
                }
            }
            Log.d("DatabaseOptimizer", "Common queries pre-compiled successfully")
        } catch (e: Exception) {
            Log.e("DatabaseOptimizer", "Error pre-compiling queries", e)
        }
    }
    fun analyzePerformance(db: SupportSQLiteDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cursor = db.query("PRAGMA table_info(transactions)")
                val columnCount = cursor.count
                cursor.close()
                val indexCursor = db.query("PRAGMA index_list(transactions)")
                val indexCount = indexCursor.count
                indexCursor.close()
                Log.d("DatabaseOptimizer", "Performance analysis: $columnCount columns, $indexCount indexes")
            } catch (e: Exception) {
                Log.e("DatabaseOptimizer", "Error analyzing performance", e)
            }
        }
    }
}