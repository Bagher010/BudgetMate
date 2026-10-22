package com.example.myapplication

import com.example.myapplication.data.model.Contact
import com.example.myapplication.data.model.ContactTransaction
import com.example.myapplication.data.model.ContactTransactionType
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

/**
 * Unit tests for Contact and ContactTransaction data models
 */
class ContactModelTest {

    @Test
    fun `contact creation with valid data succeeds`() {
        val contact = Contact(
            id = 1,
            name = "John Doe",
            phoneNumber = "+1234567890",
            email = "john@example.com",
            avatarColor = "#2196F3",
            createdAt = Date()
        )
        
        assertEquals(1L, contact.id)
        assertEquals("John Doe", contact.name)
        assertEquals("+1234567890", contact.phoneNumber)
        assertEquals("john@example.com", contact.email)
    }

    @Test
    fun `contact with empty phone number is valid`() {
        val contact = Contact(
            id = 1,
            name = "Jane Doe",
            phoneNumber = "",
            email = "jane@example.com",
            avatarColor = "#4CAF50",
            createdAt = Date()
        )
        
        assertEquals("", contact.phoneNumber)
    }

    @Test
    fun `contact with empty email is valid`() {
        val contact = Contact(
            id = 1,
            name = "Bob Smith",
            phoneNumber = "+1234567890",
            email = "",
            avatarColor = "#FF5722",
            createdAt = Date()
        )
        
        assertEquals("", contact.email)
    }

    @Test
    fun `contact transaction LENT type is correctly assigned`() {
        val transaction = ContactTransaction(
            id = 1,
            contactId = 1,
            amount = 100.0,
            description = "Lent for lunch",
            type = ContactTransactionType.LENT,
            date = Date(),
            isSettled = false,
            settledDate = null
        )
        
        assertEquals(ContactTransactionType.LENT, transaction.type)
    }

    @Test
    fun `contact transaction BORROWED type is correctly assigned`() {
        val transaction = ContactTransaction(
            id = 1,
            contactId = 1,
            amount = 50.0,
            description = "Borrowed for coffee",
            type = ContactTransactionType.BORROWED,
            date = Date(),
            isSettled = false,
            settledDate = null
        )
        
        assertEquals(ContactTransactionType.BORROWED, transaction.type)
    }

    @Test
    fun `contact transaction BILL_SPLIT type is correctly assigned`() {
        val transaction = ContactTransaction(
            id = 1,
            contactId = 1,
            amount = 25.0,
            description = "Split dinner bill",
            type = ContactTransactionType.BILL_SPLIT,
            date = Date(),
            isSettled = false,
            settledDate = null
        )
        
        assertEquals(ContactTransactionType.BILL_SPLIT, transaction.type)
    }

    @Test
    fun `contact transaction settled status is tracked`() {
        val settledDate = Date()
        val transaction = ContactTransaction(
            id = 1,
            contactId = 1,
            amount = 100.0,
            description = "Settled debt",
            type = ContactTransactionType.LENT,
            date = Date(),
            isSettled = true,
            settledDate = settledDate
        )
        
        assertTrue(transaction.isSettled)
        assertEquals(settledDate, transaction.settledDate)
    }

    @Test
    fun `contact transaction unsettled has null settledDate`() {
        val transaction = ContactTransaction(
            id = 1,
            contactId = 1,
            amount = 75.0,
            description = "Pending payment",
            type = ContactTransactionType.BORROWED,
            date = Date(),
            isSettled = false,
            settledDate = null
        )
        
        assertFalse(transaction.isSettled)
        assertNull(transaction.settledDate)
    }

    @Test
    fun `contact copy creates independent instance`() {
        val original = Contact(
            id = 1,
            name = "Original Name",
            phoneNumber = "+1111111111",
            email = "original@test.com",
            avatarColor = "#000000",
            createdAt = Date()
        )
        
        val copy = original.copy(name = "Modified Name")
        
        assertEquals("Original Name", original.name)
        assertEquals("Modified Name", copy.name)
    }

    @Test
    fun `contact transaction with zero amount is valid`() {
        val transaction = ContactTransaction(
            id = 1,
            contactId = 1,
            amount = 0.0,
            description = "Zero amount test",
            type = ContactTransactionType.BILL_SPLIT,
            date = Date(),
            isSettled = false,
            settledDate = null
        )
        
        assertEquals(0.0, transaction.amount, 0.001)
    }

    @Test
    fun `contact auto-generated id defaults to zero`() {
        val contact = Contact(
            name = "Auto ID Contact",
            phoneNumber = "",
            email = "",
            avatarColor = "#000000",
            createdAt = Date()
        )
        
        assertEquals(0L, contact.id)
    }
    
    @Test
    fun `contact transaction SETTLED type is correctly assigned`() {
        val transaction = ContactTransaction(
            id = 1,
            contactId = 1,
            amount = 50.0,
            description = "Settlement payment",
            type = ContactTransactionType.SETTLED,
            date = Date(),
            isSettled = true,
            settledDate = Date()
        )
        
        assertEquals(ContactTransactionType.SETTLED, transaction.type)
    }
}
