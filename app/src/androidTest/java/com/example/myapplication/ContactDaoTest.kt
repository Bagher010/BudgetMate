package com.example.myapplication

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.dao.ContactDao
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.model.Contact
import com.example.myapplication.data.model.ContactTransaction
import com.example.myapplication.data.model.ContactTransactionType
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
 * Instrumented tests for ContactDao
 * Tests Room database operations for contacts and contact transactions
 */
@RunWith(AndroidJUnit4::class)
class ContactDaoTest {

    private lateinit var contactDao: ContactDao
    private lateinit var db: FinanceDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FinanceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        contactDao = db.contactDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertContact_andRetrieve() = runBlocking {
        val contact = Contact(
            name = "John Doe",
            phoneNumber = "+1234567890",
            email = "john@example.com",
            avatarColor = "#2196F3",
            createdAt = Date()
        )
        
        val insertedId = contactDao.insertContact(contact)
        val retrieved = contactDao.getContactById(insertedId)
        
        assertNotNull(retrieved)
        assertEquals("John Doe", retrieved?.name)
    }

    @Test
    fun getAllContacts_returnsAll() = runBlocking {
        contactDao.insertContact(createTestContact("Contact 1"))
        contactDao.insertContact(createTestContact("Contact 2"))
        contactDao.insertContact(createTestContact("Contact 3"))
        
        val allContacts = contactDao.getAllContacts().first()
        
        assertEquals(3, allContacts.size)
    }

    @Test
    fun searchContacts_findsMatchingResults() = runBlocking {
        contactDao.insertContact(createTestContact("John Smith"))
        contactDao.insertContact(createTestContact("Jane Doe"))
        contactDao.insertContact(createTestContact("John Doe"))
        
        val searchResults = contactDao.searchContacts("John").first()
        
        assertEquals(2, searchResults.size)
    }

    @Test
    fun updateContact_modifiesData() = runBlocking {
        val contact = createTestContact("Original Name")
        val id = contactDao.insertContact(contact)
        
        val updatedContact = contact.copy(id = id, name = "Updated Name")
        contactDao.updateContact(updatedContact)
        
        val retrieved = contactDao.getContactById(id)
        assertEquals("Updated Name", retrieved?.name)
    }

    @Test
    fun deleteContact_removesFromDatabase() = runBlocking {
        val contact = createTestContact("To Delete")
        val id = contactDao.insertContact(contact)
        
        contactDao.deleteContact(contact.copy(id = id))
        
        val retrieved = contactDao.getContactById(id)
        assertNull(retrieved)
    }

    @Test
    fun insertContactTransaction_andRetrieve() = runBlocking {
        val contactId = contactDao.insertContact(createTestContact("Test Contact"))
        
        val transaction = ContactTransaction(
            contactId = contactId,
            amount = 100.0,
            description = "Lent for lunch",
            type = ContactTransactionType.LENT,
            date = Date(),
            isSettled = false,
            settledDate = null
        )
        
        val transactionId = contactDao.insertContactTransaction(transaction)
        val transactions = contactDao.getTransactionsByContact(contactId).first()
        
        assertEquals(1, transactions.size)
        assertEquals(100.0, transactions[0].amount, 0.001)
    }

    @Test
    fun getContactBalance_calculatesCorrectly() = runBlocking {
        val contactId = contactDao.insertContact(createTestContact("Balance Test"))
        
        // Lent $100 (they owe us)
        contactDao.insertContactTransaction(
            ContactTransaction(contactId = contactId, amount = 100.0, description = "Lent", 
                type = ContactTransactionType.LENT, date = Date(), 
                isSettled = false, settledDate = null)
        )
        
        // Borrowed $30 (we owe them)
        contactDao.insertContactTransaction(
            ContactTransaction(contactId = contactId, amount = 30.0, description = "Borrowed", 
                type = ContactTransactionType.BORROWED, date = Date(), 
                isSettled = false, settledDate = null)
        )
        
        val balance = contactDao.getContactBalance(contactId).first()
        
        // Balance should be 100 - 30 = 70 (they owe us $70)
        assertEquals(70.0, balance, 0.001)
    }

    @Test
    fun getUnsettledTransactions_filtersCorrectly() = runBlocking {
        val contactId = contactDao.insertContact(createTestContact("Unsettled Test"))
        
        contactDao.insertContactTransaction(
            ContactTransaction(contactId = contactId, amount = 50.0, description = "Unsettled", 
                type = ContactTransactionType.LENT, date = Date(), 
                isSettled = false, settledDate = null)
        )
        contactDao.insertContactTransaction(
            ContactTransaction(contactId = contactId, amount = 30.0, description = "Settled", 
                type = ContactTransactionType.LENT, date = Date(), 
                isSettled = true, settledDate = Date())
        )
        
        val unsettled = contactDao.getUnsettledTransactions().first()
        
        assertEquals(1, unsettled.size)
        assertFalse(unsettled[0].isSettled)
    }

    @Test
    fun settleAllTransactions_marksAsSettled() = runBlocking {
        val contactId = contactDao.insertContact(createTestContact("Settle Test"))
        
        contactDao.insertContactTransaction(
            ContactTransaction(contactId = contactId, amount = 50.0, description = "Trans 1", 
                type = ContactTransactionType.LENT, date = Date(), 
                isSettled = false, settledDate = null)
        )
        contactDao.insertContactTransaction(
            ContactTransaction(contactId = contactId, amount = 30.0, description = "Trans 2", 
                type = ContactTransactionType.BORROWED, date = Date(), 
                isSettled = false, settledDate = null)
        )
        
        val settledDate = System.currentTimeMillis()
        contactDao.settleAllTransactions(contactId, settledDate)
        
        val transactions = contactDao.getTransactionsByContact(contactId).first()
        assertTrue(transactions.all { it.isSettled })
    }

    @Test
    fun getTransactionCount_countsCorrectly() = runBlocking {
        val contactId = contactDao.insertContact(createTestContact("Count Test"))
        
        for (i in 1..5) {
            contactDao.insertContactTransaction(
                ContactTransaction(contactId = contactId, amount = i * 10.0, description = "Trans $i", 
                    type = ContactTransactionType.LENT, date = Date(), 
                    isSettled = false, settledDate = null)
            )
        }
        
        val count = contactDao.getTransactionCount(contactId).first()
        
        assertEquals(5, count)
    }

    private fun createTestContact(name: String): Contact {
        return Contact(
            name = name,
            phoneNumber = "",
            email = "",
            avatarColor = "#000000",
            createdAt = Date()
        )
    }
}
