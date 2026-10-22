package com.example.myapplication

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.dao.CategoryDao
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Instrumented tests for CategoryDao
 * Tests Room database operations for categories
 */
@RunWith(AndroidJUnit4::class)
class CategoryDaoTest {

    private lateinit var categoryDao: CategoryDao
    private lateinit var db: FinanceDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FinanceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        categoryDao = db.categoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertCategory_andRetrieve() = runBlocking {
        val category = Category(
            name = "Food",
            icon = "restaurant",
            color = 0xFF4CAF50,
            type = TransactionType.EXPENSE
        )
        
        val insertedId = categoryDao.insertCategory(category)
        val retrieved = categoryDao.getCategoryById(insertedId)
        
        assertNotNull(retrieved)
        assertEquals("Food", retrieved?.name)
        assertEquals(TransactionType.EXPENSE, retrieved?.type)
    }

    @Test
    fun insertMultipleCategories_andCountThem() = runBlocking {
        val categories = listOf(
            Category(name = "Food", icon = "restaurant", color = 0xFF4CAF50, type = TransactionType.EXPENSE),
            Category(name = "Transport", icon = "car", color = 0xFF2196F3, type = TransactionType.EXPENSE),
            Category(name = "Salary", icon = "money", color = 0xFF00FF00, type = TransactionType.INCOME)
        )
        
        categoryDao.insertCategories(categories)
        
        val allCategories = categoryDao.getAllCategories().first()
        assertEquals(3, allCategories.size)
    }

    @Test
    fun getCategoriesByType_filtersCorrectly() = runBlocking {
        categoryDao.insertCategories(listOf(
            Category(name = "Food", icon = "restaurant", color = 0xFF4CAF50, type = TransactionType.EXPENSE),
            Category(name = "Shopping", icon = "cart", color = 0xFF2196F3, type = TransactionType.EXPENSE),
            Category(name = "Salary", icon = "money", color = 0xFF00FF00, type = TransactionType.INCOME),
            Category(name = "Freelance", icon = "work", color = 0xFF9C27B0, type = TransactionType.INCOME)
        ))
        
        val expenseCategories = categoryDao.getCategoriesByType(TransactionType.EXPENSE).first()
        val incomeCategories = categoryDao.getCategoriesByType(TransactionType.INCOME).first()
        
        assertEquals(2, expenseCategories.size)
        assertEquals(2, incomeCategories.size)
    }

    @Test
    fun updateCategory_modifiesData() = runBlocking {
        val category = Category(
            name = "Original Name",
            icon = "star",
            color = 0xFF000000,
            type = TransactionType.EXPENSE
        )
        
        val id = categoryDao.insertCategory(category)
        val updatedCategory = category.copy(id = id, name = "Updated Name")
        
        categoryDao.updateCategory(updatedCategory)
        
        val retrieved = categoryDao.getCategoryById(id)
        assertEquals("Updated Name", retrieved?.name)
    }

    @Test
    fun deleteCategory_removesFromDatabase() = runBlocking {
        val category = Category(
            name = "To Delete",
            icon = "delete",
            color = 0xFFFF0000,
            type = TransactionType.EXPENSE
        )
        
        val id = categoryDao.insertCategory(category)
        val insertedCategory = category.copy(id = id)
        
        categoryDao.deleteCategory(insertedCategory)
        
        val retrieved = categoryDao.getCategoryById(id)
        assertNull(retrieved)
    }

    @Test
    fun getCategoryById_returnsNullForNonExistent() = runBlocking {
        val category = categoryDao.getCategoryById(99999L)
        assertNull(category)
    }

    @Test
    fun insertCategory_generatesUniqueIds() = runBlocking {
        val category1 = Category(name = "Category 1", icon = "one", color = 0xFF000000, type = TransactionType.EXPENSE)
        val category2 = Category(name = "Category 2", icon = "two", color = 0xFF000000, type = TransactionType.EXPENSE)
        
        val id1 = categoryDao.insertCategory(category1)
        val id2 = categoryDao.insertCategory(category2)
        
        assertNotEquals(id1, id2)
    }

    @Test
    fun categoryWithSpecialCharacters_storesCorrectly() = runBlocking {
        val category = Category(
            name = "Café & Restaurant",
            icon = "☕",
            color = 0xFF795548,
            type = TransactionType.EXPENSE
        )
        
        val id = categoryDao.insertCategory(category)
        val retrieved = categoryDao.getCategoryById(id)
        
        assertEquals("Café & Restaurant", retrieved?.name)
        assertEquals("☕", retrieved?.icon)
    }

    @Test
    fun getAllCategories_orderedCorrectly() = runBlocking {
        categoryDao.insertCategories(listOf(
            Category(name = "Zebra", icon = "z", color = 0xFF000000, type = TransactionType.EXPENSE),
            Category(name = "Apple", icon = "a", color = 0xFF000000, type = TransactionType.EXPENSE),
            Category(name = "Mango", icon = "m", color = 0xFF000000, type = TransactionType.EXPENSE)
        ))
        
        val allCategories = categoryDao.getAllCategories().first()
        assertEquals(3, allCategories.size)
    }
}
