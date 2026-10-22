package com.example.myapplication

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.dao.CategoryDao
import com.example.myapplication.data.dao.TransactionDao
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

/**
 * Instrumented tests for TransactionDao
 * Tests Room database operations for transactions
 */
@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var transactionDao: TransactionDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var db: FinanceDatabase
    private var testCategoryId: Long = 0

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FinanceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        transactionDao = db.transactionDao()
        categoryDao = db.categoryDao()
        
        // Insert a test category first (required for foreign key constraint)
        runBlocking {
            testCategoryId = categoryDao.insertCategory(
                Category(
                    name = "Test Category",
                    icon = "test",
                    color = 0xFF000000,
                    type = TransactionType.EXPENSE
                )
            )
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertTransaction_andRetrieve() = runBlocking {
        val transaction = Transaction(
            amount = 100.0,
            description = "Test transaction",
            date = Date(),
            categoryId = testCategoryId,
            type = TransactionType.EXPENSE
        )
        
        val insertedId = transactionDao.insertTransaction(transaction)
        
        assertTrue(insertedId > 0)
    }

    @Test
    fun insertMultipleTransactions_andCountThem() = runBlocking {
        val transactions = listOf(
            Transaction(amount = 50.0, description = "Transaction 1", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE),
            Transaction(amount = 75.0, description = "Transaction 2", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE),
            Transaction(amount = 100.0, description = "Transaction 3", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        
        transactions.forEach { transactionDao.insertTransaction(it) }
        
        val allTransactions = transactionDao.getAllTransactionsWithCategory().first()
        assertEquals(3, allTransactions.size)
    }

    @Test
    fun deleteTransaction_removesFromDatabase() = runBlocking {
        val transaction = Transaction(
            amount = 100.0,
            description = "To be deleted",
            date = Date(),
            categoryId = testCategoryId,
            type = TransactionType.EXPENSE
        )
        
        val id = transactionDao.insertTransaction(transaction)
        val insertedTransaction = transaction.copy(id = id)
        
        transactionDao.deleteTransaction(insertedTransaction)
        
        val allTransactions = transactionDao.getAllTransactionsWithCategory().first()
        assertEquals(0, allTransactions.size)
    }

    @Test
    fun updateTransaction_modifiesData() = runBlocking {
        val transaction = Transaction(
            amount = 100.0,
            description = "Original description",
            date = Date(),
            categoryId = testCategoryId,
            type = TransactionType.EXPENSE
        )
        
        val id = transactionDao.insertTransaction(transaction)
        val updatedTransaction = transaction.copy(
            id = id,
            amount = 200.0,
            description = "Updated description"
        )
        
        transactionDao.updateTransaction(updatedTransaction)
        
        val retrieved = transactionDao.getAllTransactionsWithCategory().first()
        assertEquals(1, retrieved.size)
        assertEquals(200.0, retrieved[0].transaction.amount, 0.001)
        assertEquals("Updated description", retrieved[0].transaction.description)
    }

    @Test
    fun getTotalByType_calculatesCorrectly() = runBlocking {
        // Add income category
        val incomeCategoryId = categoryDao.insertCategory(
            Category(name = "Income Category", icon = "money", color = 0xFF00FF00, type = TransactionType.INCOME)
        )
        
        // Insert expense transactions
        transactionDao.insertTransaction(
            Transaction(amount = 50.0, description = "Expense 1", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        transactionDao.insertTransaction(
            Transaction(amount = 30.0, description = "Expense 2", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        
        // Insert income transaction
        transactionDao.insertTransaction(
            Transaction(amount = 1000.0, description = "Income 1", date = Date(), categoryId = incomeCategoryId, type = TransactionType.INCOME)
        )
        
        val totalExpense = transactionDao.getTotalByType(TransactionType.EXPENSE).first()
        val totalIncome = transactionDao.getTotalByType(TransactionType.INCOME).first()
        
        assertEquals(80.0, totalExpense ?: 0.0, 0.001)
        assertEquals(1000.0, totalIncome ?: 0.0, 0.001)
    }

    @Test
    fun searchTransactions_findsMatchingResults() = runBlocking {
        transactionDao.insertTransaction(
            Transaction(amount = 50.0, description = "Grocery shopping", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        transactionDao.insertTransaction(
            Transaction(amount = 30.0, description = "Gas station", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        transactionDao.insertTransaction(
            Transaction(amount = 100.0, description = "Online shopping", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        
        val searchResults = transactionDao.searchTransactions("shopping")
        
        assertEquals(2, searchResults.size)
    }

    @Test
    fun getRecentTransactions_limitsResults() = runBlocking {
        for (i in 1..10) {
            transactionDao.insertTransaction(
                Transaction(amount = i * 10.0, description = "Transaction $i", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
            )
        }
        
        val recentTransactions = transactionDao.getRecentTransactionsWithCategory(5).first()
        
        assertEquals(5, recentTransactions.size)
    }

    @Test
    fun getBalanceSummary_calculatesCorrectly() = runBlocking {
        // Add income category
        val incomeCategoryId = categoryDao.insertCategory(
            Category(name = "Salary", icon = "money", color = 0xFF00FF00, type = TransactionType.INCOME)
        )
        
        // Insert transactions
        transactionDao.insertTransaction(
            Transaction(amount = 1000.0, description = "Salary", date = Date(), categoryId = incomeCategoryId, type = TransactionType.INCOME)
        )
        transactionDao.insertTransaction(
            Transaction(amount = 200.0, description = "Rent", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        transactionDao.insertTransaction(
            Transaction(amount = 50.0, description = "Food", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        
        val summary = transactionDao.getBalanceSummary().first()
        
        assertEquals(1000.0, summary.totalIncome, 0.001)
        assertEquals(250.0, summary.totalExpense, 0.001)
        assertEquals(750.0, summary.balance, 0.001)
    }

    @Test
    fun transactionsByType_filtersCorrectly() = runBlocking {
        // Add income category
        val incomeCategoryId = categoryDao.insertCategory(
            Category(name = "Income", icon = "money", color = 0xFF00FF00, type = TransactionType.INCOME)
        )
        
        transactionDao.insertTransaction(
            Transaction(amount = 100.0, description = "Income 1", date = Date(), categoryId = incomeCategoryId, type = TransactionType.INCOME)
        )
        transactionDao.insertTransaction(
            Transaction(amount = 50.0, description = "Expense 1", date = Date(), categoryId = testCategoryId, type = TransactionType.EXPENSE)
        )
        transactionDao.insertTransaction(
            Transaction(amount = 200.0, description = "Income 2", date = Date(), categoryId = incomeCategoryId, type = TransactionType.INCOME)
        )
        
        val incomeTransactions = transactionDao.getTransactionsByType(TransactionType.INCOME).first()
        val expenseTransactions = transactionDao.getTransactionsByType(TransactionType.EXPENSE).first()
        
        assertEquals(2, incomeTransactions.size)
        assertEquals(1, expenseTransactions.size)
    }
}
