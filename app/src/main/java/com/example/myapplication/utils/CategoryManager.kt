package com.example.myapplication.utils
import android.content.Context
import android.util.Log
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.CategoryDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
object CategoryManager {
    private var isInitialized = false
    fun ensureDefaultCategories(context: Context) {
        if (isInitialized) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = FinanceDatabase.getDatabase(context)
                val categoryDao = database.categoryDao()
                val defaultCategories = CategoryDataSource.getDefaultCategories()
                categoryDao.insertCategories(defaultCategories)
                Log.d("CategoryManager", "Default categories ensured: ${defaultCategories.size} categories")
                isInitialized = true
            } catch (e: Exception) {
                Log.e("CategoryManager", "Error ensuring default categories", e)
            }
        }
    }
    fun getDefaultCategories(): List<Category> {
        return CategoryDataSource.getDefaultCategories()
    }
    fun getIncomeCategories(): List<Category> {
        return CategoryDataSource.getIncomeCategories()
    }
    fun getExpenseCategories(): List<Category> {
        return CategoryDataSource.getExpenseCategories()
    }
    fun isInitialized(): Boolean {
        return isInitialized
    }
    fun reset() {
        isInitialized = false
    }
}