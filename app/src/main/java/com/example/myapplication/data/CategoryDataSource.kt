package com.example.myapplication.data
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.TransactionType
object CategoryDataSource {
    fun getDefaultCategories(): List<Category> {
        return listOf(
            Category(name = "Salary", icon = "💰", color = 0xFF00C853, type = TransactionType.INCOME),
            Category(name = "Freelance", icon = "💻", color = 0xFF2196F3, type = TransactionType.INCOME),
            Category(name = "Investment", icon = "📈", color = 0xFF9C27B0, type = TransactionType.INCOME),
            Category(name = "Gift", icon = "🎁", color = 0xFFE91E63, type = TransactionType.INCOME),
            Category(name = "Other Income", icon = "💵", color = 0xFF009688, type = TransactionType.INCOME),
            Category(name = "Food", icon = "🍕", color = 0xFFFF9800, type = TransactionType.EXPENSE),
            Category(name = "Transport", icon = "🚗", color = 0xFF3F51B5, type = TransactionType.EXPENSE),
            Category(name = "Shopping", icon = "🛍️", color = 0xFFE91E63, type = TransactionType.EXPENSE),
            Category(name = "Entertainment", icon = "🎬", color = 0xFF9C27B0, type = TransactionType.EXPENSE),
            Category(name = "Health", icon = "🏥", color = 0xFFF44336, type = TransactionType.EXPENSE),
            Category(name = "Education", icon = "📚", color = 0xFF2196F3, type = TransactionType.EXPENSE),
            Category(name = "Bills", icon = "📄", color = 0xFF795548, type = TransactionType.EXPENSE),
            Category(name = "Rent", icon = "🏠", color = 0xFF607D8B, type = TransactionType.EXPENSE),
            Category(name = "Other Expense", icon = "💸", color = 0xFF424242, type = TransactionType.EXPENSE)
        )
    }
    fun getIncomeCategories(): List<Category> {
        return getDefaultCategories().filter { it.type == TransactionType.INCOME }
    }
    fun getExpenseCategories(): List<Category> {
        return getDefaultCategories().filter { it.type == TransactionType.EXPENSE }
    }
}