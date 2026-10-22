package com.example.myapplication.data.dao
import androidx.room.*
import com.example.myapplication.data.model.Contact
import com.example.myapplication.data.model.ContactTransaction
import com.example.myapplication.data.model.ContactTransactionType
import kotlinx.coroutines.flow.Flow
@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<Contact>>
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Long): Contact?
    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%'")
    fun searchContacts(query: String): Flow<List<Contact>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long
    @Update
    suspend fun updateContact(contact: Contact)
    @Delete
    suspend fun deleteContact(contact: Contact)
    @Query("SELECT * FROM contact_transactions WHERE contactId = :contactId ORDER BY date DESC")
    fun getTransactionsByContact(contactId: Long): Flow<List<ContactTransaction>>
    @Query("SELECT * FROM contact_transactions ORDER BY date DESC")
    fun getAllContactTransactions(): Flow<List<ContactTransaction>>
    @Query("SELECT * FROM contact_transactions WHERE isSettled = 0 ORDER BY date DESC")
    fun getUnsettledTransactions(): Flow<List<ContactTransaction>>
    @Query("SELECT * FROM contact_transactions WHERE id = :id")
    suspend fun getContactTransactionById(id: Long): ContactTransaction?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactTransaction(transaction: ContactTransaction): Long
    @Update
    suspend fun updateContactTransaction(transaction: ContactTransaction)
    @Delete
    suspend fun deleteContactTransaction(transaction: ContactTransaction)
    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN type = 'LENT' THEN amount
                WHEN type = 'BORROWED' THEN -amount
                WHEN type = 'BILL_SPLIT' AND amount > 0 THEN amount
                WHEN type = 'BILL_SPLIT' AND amount < 0 THEN amount
                WHEN type = 'SETTLED' THEN -amount
                ELSE 0
            END
        ), 0.0) FROM contact_transactions WHERE contactId = :contactId AND isSettled = 0
    """)
    fun getContactBalance(contactId: Long): Flow<Double>
    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN type = 'LENT' THEN amount
                WHEN type = 'BILL_SPLIT' AND amount > 0 THEN amount
                ELSE 0
            END
        ), 0.0) FROM contact_transactions WHERE contactId = :contactId AND isSettled = 0
    """)
    fun getTotalOwedByContact(contactId: Long): Flow<Double>
    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN type = 'BORROWED' THEN amount
                WHEN type = 'BILL_SPLIT' AND amount < 0 THEN -amount
                ELSE 0
            END
        ), 0.0) FROM contact_transactions WHERE contactId = :contactId AND isSettled = 0
    """)
    fun getTotalOwingToContact(contactId: Long): Flow<Double>
    @Query("SELECT COUNT(*) FROM contact_transactions WHERE contactId = :contactId")
    fun getTransactionCount(contactId: Long): Flow<Int>
    @Query("UPDATE contact_transactions SET isSettled = 1, settledDate = :settledDate WHERE contactId = :contactId AND isSettled = 0")
    suspend fun settleAllTransactions(contactId: Long, settledDate: Long)
    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN type = 'LENT' THEN amount
                WHEN type = 'BILL_SPLIT' AND amount > 0 THEN amount
                ELSE 0
            END
        ), 0.0) FROM contact_transactions WHERE isSettled = 0
    """)
    fun getTotalOwedToUser(): Flow<Double>
    @Query("""
        SELECT COALESCE(SUM(
            CASE 
                WHEN type = 'BORROWED' THEN amount
                WHEN type = 'BILL_SPLIT' AND amount < 0 THEN -amount
                ELSE 0
            END
        ), 0.0) FROM contact_transactions WHERE isSettled = 0
    """)
    fun getTotalUserOwes(): Flow<Double>
}