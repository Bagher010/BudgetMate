package com.example.myapplication.benchmark

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.TransactionType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var database: FinanceDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, FinanceDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        runBlocking {
            setupTestData()
        }
    }

    @After
    fun teardown() {
        database.close()
    }

    private suspend fun setupTestData() {
        val categories = listOf(
            Category(1, "Food", "🍕", 0xFF4CAF50, TransactionType.EXPENSE),
            Category(2, "Transport", "🚗", 0xFF2196F3, TransactionType.EXPENSE),
            Category(3, "Salary", "💰", 0xFFFF9800, TransactionType.INCOME),
            Category(4, "Shopping", "🛒", 0xFF9C27B0, TransactionType.EXPENSE)
        )

        database.categoryDao().insertCategories(categories)

        val transactions = mutableListOf<Transaction>()
        val random = Random()
        val startDate = Calendar.getInstance().apply { 
            add(Calendar.MONTH, -12) 
        }.time

        repeat(1000) { i ->
            val category = categories[random.nextInt(categories.size)]
            val date = Date(startDate.time + (random.nextLong() % (365L * 24 * 60 * 60 * 1000)))

            transactions.add(
                Transaction(
                    id = i.toLong() + 1,
                    amount = random.nextDouble() * 1000,
                    description = "Transaction $i",
                    date = date,
                    categoryId = category.id,
                    type = category.type
                )
            )
        }

        transactions.forEach { transaction ->
            database.transactionDao().insertTransaction(transaction)
        }
    }

    @Test
    fun benchmarkGetAllTransactions() {
        benchmarkRule.measureRepeated {
            runBlocking {
                database.transactionDao().getAllTransactions()
            }
        }
    }

    @Test
    fun benchmarkGetTransactionsWithCategory() {
        benchmarkRule.measureRepeated {
            runBlocking {
                database.transactionDao().getAllTransactionsWithCategory()
            }
        }
    }

    @Test
    fun benchmarkGetRecentTransactionsOptimized() {
        benchmarkRule.measureRepeated {
            runBlocking {
                database.transactionDao().getRecentTransactionsOptimized(20)
            }
        }
    }

    @Test
    fun benchmarkGetBalanceSummary() {
        benchmarkRule.measureRepeated {
            runBlocking {
                database.transactionDao().getBalanceSummary()
            }
        }
    }

    @Test
    fun benchmarkSearchTransactions() {
        benchmarkRule.measureRepeated {
            runBlocking {
                database.transactionDao().searchTransactions("Transaction", 50)
            }
        }
    }

    @Test
    fun benchmarkGetCategoryTotalsOptimized() {
        val startDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time
        val endDate = Date()

        benchmarkRule.measureRepeated {
            runBlocking {
                database.transactionDao().getCategoryTotalsOptimized(
                    TransactionType.EXPENSE, 
                    startDate, 
                    endDate
                )
            }
        }
    }

    @Test
    fun benchmarkInsertTransaction() {
        val category = Category(99, "Test", "🧪", 0xFF000000, TransactionType.EXPENSE)

        benchmarkRule.measureRepeated {
            runBlocking {
                database.transactionDao().insertTransaction(
                    Transaction(
                        amount = 100.0,
                        description = "Benchmark test",
                        date = Date(),
                        categoryId = 1,
                        type = TransactionType.EXPENSE
                    )
                )
            }
        }
    }
}
