package com.example.myapplication

import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.TransactionType
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Category data model validation
 */
class CategoryModelTest {

    @Test
    fun `category creation with valid data succeeds`() {
        val category = Category(
            id = 1,
            name = "Food",
            icon = "restaurant",
            color = 0xFF4CAF50,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(1L, category.id)
        assertEquals("Food", category.name)
        assertEquals("restaurant", category.icon)
        assertEquals(TransactionType.EXPENSE, category.type)
    }

    @Test
    fun `expense category type is correctly assigned`() {
        val category = Category(
            id = 1,
            name = "Shopping",
            icon = "shopping_cart",
            color = 0xFF2196F3,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(TransactionType.EXPENSE, category.type)
    }

    @Test
    fun `income category type is correctly assigned`() {
        val category = Category(
            id = 1,
            name = "Salary",
            icon = "account_balance",
            color = 0xFF4CAF50,
            type = TransactionType.INCOME
        )
        
        assertEquals(TransactionType.INCOME, category.type)
    }

    @Test
    fun `category with emoji icon is valid`() {
        val category = Category(
            id = 1,
            name = "Entertainment",
            icon = "🎬",
            color = 0xFFE91E63,
            type = TransactionType.EXPENSE
        )
        
        assertEquals("🎬", category.icon)
    }

    @Test
    fun `category color stores correctly`() {
        val expectedColor = 0xFFFF5722
        val category = Category(
            id = 1,
            name = "Transportation",
            icon = "directions_car",
            color = expectedColor,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(expectedColor, category.color)
    }

    @Test
    fun `category copy creates independent instance`() {
        val original = Category(
            id = 1,
            name = "Original",
            icon = "star",
            color = 0xFF000000,
            type = TransactionType.EXPENSE
        )
        
        val copy = original.copy(name = "Modified")
        
        assertEquals("Original", original.name)
        assertEquals("Modified", copy.name)
    }

    @Test
    fun `category equals works correctly`() {
        val category1 = Category(
            id = 1,
            name = "Test",
            icon = "test",
            color = 0xFF000000,
            type = TransactionType.EXPENSE
        )
        
        val category2 = Category(
            id = 1,
            name = "Test",
            icon = "test",
            color = 0xFF000000,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(category1, category2)
    }

    @Test
    fun `category with long name is valid`() {
        val longName = "This is a very long category name for testing purposes"
        val category = Category(
            id = 1,
            name = longName,
            icon = "category",
            color = 0xFF9C27B0,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(longName, category.name)
    }

    @Test
    fun `category auto-generated id defaults to zero`() {
        val category = Category(
            name = "Auto ID",
            icon = "auto",
            color = 0xFF000000,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(0L, category.id)
    }
}
