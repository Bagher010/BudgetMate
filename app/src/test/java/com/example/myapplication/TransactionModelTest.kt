package com.example.myapplication

import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.TransactionType
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

/**
 * Unit tests for Transaction data model validation
 */
class TransactionModelTest {

    @Test
    fun `transaction creation with valid data succeeds`() {
        val date = Date()
        val transaction = Transaction(
            id = 1,
            amount = 100.0,
            description = "Test Transaction",
            date = date,
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(1L, transaction.id)
        assertEquals(100.0, transaction.amount, 0.001)
        assertEquals("Test Transaction", transaction.description)
        assertEquals(date, transaction.date)
        assertEquals(1L, transaction.categoryId)
        assertEquals(TransactionType.EXPENSE, transaction.type)
    }

    @Test
    fun `transaction with zero amount is valid`() {
        val transaction = Transaction(
            id = 1,
            amount = 0.0,
            description = "Zero amount",
            date = Date(),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(0.0, transaction.amount, 0.001)
    }

    @Test
    fun `transaction with negative amount is technically valid at model level`() {
        val transaction = Transaction(
            id = 1,
            amount = -50.0,
            description = "Negative amount",
            date = Date(),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(-50.0, transaction.amount, 0.001)
    }

    @Test
    fun `transaction with large amount handles correctly`() {
        val largeAmount = 999999999.99
        val transaction = Transaction(
            id = 1,
            amount = largeAmount,
            description = "Large amount",
            date = Date(),
            categoryId = 1,
            type = TransactionType.INCOME
        )
        
        assertEquals(largeAmount, transaction.amount, 0.001)
    }

    @Test
    fun `transaction with empty description is valid`() {
        val transaction = Transaction(
            id = 1,
            amount = 50.0,
            description = "",
            date = Date(),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals("", transaction.description)
    }

    @Test
    fun `transaction type INCOME is correctly assigned`() {
        val transaction = Transaction(
            id = 1,
            amount = 1000.0,
            description = "Salary",
            date = Date(),
            categoryId = 1,
            type = TransactionType.INCOME
        )
        
        assertEquals(TransactionType.INCOME, transaction.type)
    }

    @Test
    fun `transaction type EXPENSE is correctly assigned`() {
        val transaction = Transaction(
            id = 1,
            amount = 50.0,
            description = "Groceries",
            date = Date(),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(TransactionType.EXPENSE, transaction.type)
    }

    @Test
    fun `transaction copy creates independent instance`() {
        val original = Transaction(
            id = 1,
            amount = 100.0,
            description = "Original",
            date = Date(),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        val copy = original.copy(amount = 200.0)
        
        assertEquals(100.0, original.amount, 0.001)
        assertEquals(200.0, copy.amount, 0.001)
        assertEquals(original.description, copy.description)
    }

    @Test
    fun `transaction equals works correctly`() {
        val date = Date()
        val transaction1 = Transaction(
            id = 1,
            amount = 100.0,
            description = "Test",
            date = date,
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        val transaction2 = Transaction(
            id = 1,
            amount = 100.0,
            description = "Test",
            date = date,
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(transaction1, transaction2)
    }

    @Test
    fun `transaction with special characters in description`() {
        val transaction = Transaction(
            id = 1,
            amount = 50.0,
            description = "Café & Restaurant - 50% off!",
            date = Date(),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals("Café & Restaurant - 50% off!", transaction.description)
    }

    @Test
    fun `transaction auto-generated id defaults to zero`() {
        val transaction = Transaction(
            amount = 100.0,
            description = "Auto ID",
            date = Date(),
            categoryId = 1,
            type = TransactionType.EXPENSE
        )
        
        assertEquals(0L, transaction.id)
    }
}
