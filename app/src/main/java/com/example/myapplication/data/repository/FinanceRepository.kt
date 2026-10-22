package com.example.myapplication.data.repository
import com.example.myapplication.data.dao.CategoryDao
import com.example.myapplication.data.dao.ContactDao
import com.example.myapplication.data.dao.TransactionDao
import com.example.myapplication.data.dao.SubscriptionDao
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.Contact
import com.example.myapplication.data.model.ContactTransaction
import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.model.TransactionWithCategory
import com.example.myapplication.data.model.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import java.util.Date
class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val subscriptionDao: SubscriptionDao,
    private val contactDao: ContactDao
) {
    fun getAllTransactionsWithCategory(): Flow<List<TransactionWithCategory>> =
        transactionDao.getAllTransactionsWithCategory()
            .conflate() 
            .flowOn(Dispatchers.IO)
    fun getRecentTransactionsWithCategory(limit: Int): Flow<List<TransactionWithCategory>> =
        transactionDao.getRecentTransactionsWithCategory(limit)
            .conflate()
            .flowOn(Dispatchers.IO)
    suspend fun getTransactionsPaginated(
        startDate: Date,
        endDate: Date,
        limit: Int,
        offset: Int
    ): List<TransactionWithCategory> = withContext(Dispatchers.IO) {
        transactionDao.getTransactionsPaginated(startDate, endDate, limit, offset)
    }
    suspend fun searchTransactions(query: String): List<TransactionWithCategory> = withContext(Dispatchers.IO) {
        transactionDao.searchTransactions(query)
    }
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionWithCategory>> =
        transactionDao.getTransactionsByType(type)
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<TransactionWithCategory>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)
    fun getTotalByType(type: TransactionType): Flow<Double?> =
        transactionDao.getTotalByType(type)
    suspend fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Double? =
        transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)
    suspend fun insertTransaction(transaction: Transaction): Long = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(transaction)
    }
    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)
    fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories()
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.getCategoriesByType(type)
    suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)
    suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category)
    suspend fun insertCategories(categories: List<Category>) =
        categoryDao.insertCategories(categories)
    suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category)
    fun getBalanceFlow(): Flow<com.example.myapplication.ui.viewmodel.BalanceData> {
        return transactionDao.getBalanceSummary().map { result ->
            com.example.myapplication.ui.viewmodel.BalanceData(
                balance = result.balance,
                income = result.totalIncome,
                expense = result.totalExpense
            )
        }
    }
    fun getAllSubscriptions(): Flow<List<Subscription>> =
        subscriptionDao.getAllSubscriptions()
            .conflate()
            .flowOn(Dispatchers.IO)
    fun getActiveSubscriptions(): Flow<List<Subscription>> =
        subscriptionDao.getActiveSubscriptions()
            .conflate()
            .flowOn(Dispatchers.IO)
    fun getInactiveSubscriptions(): Flow<List<Subscription>> =
        subscriptionDao.getInactiveSubscriptions()
            .conflate()
            .flowOn(Dispatchers.IO)
    suspend fun getSubscriptionById(id: Long): Subscription? = withContext(Dispatchers.IO) {
        subscriptionDao.getSubscriptionById(id)
    }
    fun getSubscriptionsByCategory(category: String): Flow<List<Subscription>> =
        subscriptionDao.getSubscriptionsByCategory(category)
    fun getMonthlyEquivalentTotal(): Flow<Double?> =
        subscriptionDao.getMonthlyEquivalentTotal()
    fun getActiveSubscriptionCount(): Flow<Int> =
        subscriptionDao.getActiveSubscriptionCount()
    fun getUpcomingSubscriptions(startDate: Long, endDate: Long): Flow<List<Subscription>> =
        subscriptionDao.getUpcomingSubscriptions(startDate, endDate)
    suspend fun insertSubscription(subscription: Subscription): Long = withContext(Dispatchers.IO) {
        subscriptionDao.insertSubscription(subscription)
    }
    suspend fun updateSubscription(subscription: Subscription) = withContext(Dispatchers.IO) {
        subscriptionDao.updateSubscription(subscription)
    }
    suspend fun deleteSubscription(subscription: Subscription) = withContext(Dispatchers.IO) {
        subscriptionDao.deleteSubscription(subscription)
    }
    suspend fun updateSubscriptionStatus(id: Long, isActive: Boolean) = withContext(Dispatchers.IO) {
        subscriptionDao.updateSubscriptionStatus(id, isActive)
    }
    fun getAllContacts(): Flow<List<Contact>> =
        contactDao.getAllContacts()
            .conflate()
            .flowOn(Dispatchers.IO)
    suspend fun getContactById(id: Long): Contact? = withContext(Dispatchers.IO) {
        contactDao.getContactById(id)
    }
    fun searchContacts(query: String): Flow<List<Contact>> =
        contactDao.searchContacts(query)
            .conflate()
            .flowOn(Dispatchers.IO)
    suspend fun insertContact(contact: Contact): Long = withContext(Dispatchers.IO) {
        contactDao.insertContact(contact)
    }
    suspend fun updateContact(contact: Contact) = withContext(Dispatchers.IO) {
        contactDao.updateContact(contact)
    }
    suspend fun deleteContact(contact: Contact) = withContext(Dispatchers.IO) {
        contactDao.deleteContact(contact)
    }
    fun getTransactionsByContact(contactId: Long): Flow<List<ContactTransaction>> =
        contactDao.getTransactionsByContact(contactId)
            .conflate()
            .flowOn(Dispatchers.IO)
    fun getAllContactTransactions(): Flow<List<ContactTransaction>> =
        contactDao.getAllContactTransactions()
            .conflate()
            .flowOn(Dispatchers.IO)
    fun getUnsettledTransactions(): Flow<List<ContactTransaction>> =
        contactDao.getUnsettledTransactions()
            .conflate()
            .flowOn(Dispatchers.IO)
    suspend fun insertContactTransaction(transaction: ContactTransaction): Long = withContext(Dispatchers.IO) {
        contactDao.insertContactTransaction(transaction)
    }
    suspend fun updateContactTransaction(transaction: ContactTransaction) = withContext(Dispatchers.IO) {
        contactDao.updateContactTransaction(transaction)
    }
    suspend fun deleteContactTransaction(transaction: ContactTransaction) = withContext(Dispatchers.IO) {
        contactDao.deleteContactTransaction(transaction)
    }
    fun getContactBalance(contactId: Long): Flow<Double> =
        contactDao.getContactBalance(contactId)
    fun getTotalOwedByContact(contactId: Long): Flow<Double> =
        contactDao.getTotalOwedByContact(contactId)
    fun getTotalOwingToContact(contactId: Long): Flow<Double> =
        contactDao.getTotalOwingToContact(contactId)
    fun getTransactionCount(contactId: Long): Flow<Int> =
        contactDao.getTransactionCount(contactId)
    suspend fun settleAllTransactions(contactId: Long, settledDate: Long) = withContext(Dispatchers.IO) {
        contactDao.settleAllTransactions(contactId, settledDate)
    }
    fun getTotalOwedToUser(): Flow<Double> =
        contactDao.getTotalOwedToUser()
    fun getTotalUserOwes(): Flow<Double> =
        contactDao.getTotalUserOwes()
}