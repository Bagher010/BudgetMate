package com.example.myapplication.data.database
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import android.util.Log
import com.example.myapplication.data.dao.CategoryDao
import com.example.myapplication.data.dao.ContactDao
import com.example.myapplication.data.dao.TransactionDao
import com.example.myapplication.data.dao.SubscriptionDao
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.Contact
import com.example.myapplication.data.model.ContactTransaction
import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.Subscription
import com.example.myapplication.data.CategoryDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
@Database(
    entities = [Transaction::class, Category::class, Subscription::class, Contact::class, ContactTransaction::class],
    version = 3, 
    exportSchema = true,
    autoMigrations = [] 
)
@TypeConverters(Converters::class)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun contactDao(): ContactDao
    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null
        private const val DATABASE_NAME = "finance_db_with_subscriptions"
        fun getDatabase(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    DATABASE_NAME
                )
                Log.d("FinanceDatabase", "Creating database with essential categories only")
                val instance = builder
                .setJournalMode(JournalMode.WRITE_AHEAD_LOGGING) 
                .enableMultiInstanceInvalidation() 
                .setQueryExecutor(Executors.newFixedThreadPool(2)) 
                .setTransactionExecutor(Executors.newSingleThreadExecutor()) 
                .openHelperFactory(null) 
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d("FinanceDatabase", "Creating database with essential categories only")
                        prePopulateEssentialCategories(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date)")
                                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_category_date ON transactions(categoryId, date)")
                                db.execSQL("CREATE INDEX IF NOT EXISTS idx_transactions_type_date ON transactions(type, date)")
                                Log.d("FinanceDatabase", "Additional indexes created successfully")
                            } catch (e: Exception) {
                                Log.e("FinanceDatabase", "Error creating additional indexes", e)
                            }
                        }
                    }
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        DatabasePerformanceOptimizer.optimizeForStartup(db)
                        DatabasePerformanceOptimizer.createOptimizedIndexes(db)
                        DatabasePerformanceOptimizer.analyzePerformance(db)
                        val cursor = db.query("SELECT COUNT(*) FROM categories")
                        if (cursor.moveToFirst()) {
                            val count = cursor.getInt(0)
                            cursor.close()
                            if (count == 0) {
                                Log.d("FinanceDatabase", "No categories found on open, populating...")
                                prePopulateEssentialCategories(db)
                            } else {
                                Log.d("FinanceDatabase", "Categories already exist: $count")
                            }
                        }
                        Log.d("FinanceDatabase", "Database opened with comprehensive optimizations")
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
        private fun prePopulateEssentialCategories(db: SupportSQLiteDatabase) {
            try {
                val essentialCategories = CategoryDataSource.getDefaultCategories()
                Log.d("FinanceDatabase", "Attempting to insert ${essentialCategories.size} categories")
                var insertedCount = 0
                essentialCategories.forEach { category ->
                    try {
                        db.execSQL("""
                            INSERT OR IGNORE INTO categories (name, icon, color, type) 
                            VALUES (?, ?, ?, ?)
                        """.trimIndent(), arrayOf<Any>(
                            category.name,
                            category.icon,
                            category.color,
                            category.type.name
                        ))
                        insertedCount++
                    } catch (e: Exception) {
                        Log.e("FinanceDatabase", "Error inserting category ${category.name}", e)
                    }
                }
                val cursor = db.query("SELECT COUNT(*) FROM categories")
                if (cursor.moveToFirst()) {
                    val count = cursor.getInt(0)
                    Log.d("FinanceDatabase", "Categories in database: $count (inserted: $insertedCount)")
                    cursor.close()
                }
                Log.d("FinanceDatabase", "Essential categories pre-populated successfully")
            } catch (e: Exception) {
                Log.e("FinanceDatabase", "Error pre-populating essential categories", e)
                e.printStackTrace()
            }
        }
        fun warmUpDatabase(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = getDatabase(context)
                    DatabasePerformanceOptimizer.warmUpDatabase(context, db)
                    Log.d("FinanceDatabase", "Database warmed up with performance optimizations")
                } catch (e: Exception) {
                    Log.e("FinanceDatabase", "Error warming up database", e)
                }
            }
        }
    }
}
