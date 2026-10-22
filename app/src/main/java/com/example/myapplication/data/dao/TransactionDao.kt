package com.example.myapplication.data.dao
import androidx.room.*
import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.TransactionWithCategory
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.model.CategoryTotalResult
import com.example.myapplication.data.model.MonthlySummaryResult
import com.example.myapplication.data.model.TransactionListItem
import com.example.myapplication.data.model.BalanceSummaryResult
import kotlinx.coroutines.flow.Flow
import java.util.Date
@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsWithCategory(): Flow<List<TransactionWithCategory>>
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactionsWithCategory(limit: Int): Flow<List<TransactionWithCategory>>
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<TransactionWithCategory>>
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<TransactionWithCategory>>
    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :query || '%' ORDER BY date DESC")
    suspend fun searchTransactions(query: String): List<TransactionWithCategory>
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    fun getTotalByType(type: TransactionType): Flow<Double?>
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Double?
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long
    @Update
    suspend fun updateTransaction(transaction: Transaction)
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
    @androidx.room.Transaction
    @Query("""
        SELECT * FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate 
        ORDER BY date DESC, id DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getTransactionsPaginated(
        startDate: Date, 
        endDate: Date, 
        limit: Int, 
        offset: Int
    ): List<TransactionWithCategory>
    @androidx.room.Transaction
    @Query("""
        SELECT * FROM transactions 
        WHERE description LIKE '%' || :searchTerm || '%' 
        OR EXISTS (
            SELECT 1 FROM categories c 
            WHERE c.id = transactions.categoryId 
            AND c.name LIKE '%' || :searchTerm || '%'
        )
        ORDER BY date DESC
        LIMIT :limit
    """)
    fun searchTransactions(searchTerm: String, limit: Int = 50): Flow<List<TransactionWithCategory>>
    @Query("""
        SELECT 
            c.id as categoryId,
            c.name as categoryName,
            c.icon as categoryIcon,
            c.color as categoryColor,
            SUM(t.amount) as total,
            COUNT(*) as transactionCount
        FROM transactions t 
        JOIN categories c ON t.categoryId = c.id 
        WHERE t.type = :type AND t.date BETWEEN :startDate AND :endDate
        GROUP BY c.id, c.name, c.icon, c.color
        ORDER BY total DESC
    """)
    suspend fun getCategoryTotalsOptimized(
        type: TransactionType, 
        startDate: Date, 
        endDate: Date
    ): List<CategoryTotalResult>
    @Query("""
        SELECT 
            strftime('%Y-%m', date/1000, 'unixepoch') as month,
            type,
            SUM(amount) as total,
            COUNT(*) as count,
            AVG(amount) as average
        FROM transactions 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY month, type
        ORDER BY month DESC
    """)
    suspend fun getMonthlySummary(startDate: Date, endDate: Date): List<MonthlySummaryResult>
    @Query("SELECT COUNT(*) FROM transactions WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTransactionCountByDateRange(startDate: Date, endDate: Date): Int
    @Query("""
        SELECT t.id, t.amount, t.description, t.date, t.type, 
               c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor
        FROM transactions t 
        JOIN categories c ON t.categoryId = c.id 
        ORDER BY t.date DESC, t.id DESC
        LIMIT :limit
    """)
    fun getRecentTransactionsOptimized(limit: Int): Flow<List<TransactionListItem>>
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as totalIncome,
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as totalExpense,
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0) as balance
        FROM transactions
    """)
    fun getBalanceSummary(): Flow<BalanceSummaryResult>
}