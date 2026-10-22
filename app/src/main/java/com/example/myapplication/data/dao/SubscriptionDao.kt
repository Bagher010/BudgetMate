package com.example.myapplication.data.dao
import androidx.room.*
import com.example.myapplication.data.model.Subscription
import kotlinx.coroutines.flow.Flow
@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY nextPaymentDate ASC")
    fun getAllSubscriptions(): Flow<List<Subscription>>
    @Query("SELECT * FROM subscriptions WHERE isActive = 1 ORDER BY nextPaymentDate ASC")
    fun getActiveSubscriptions(): Flow<List<Subscription>>
    @Query("SELECT * FROM subscriptions WHERE isActive = 0 ORDER BY name ASC")
    fun getInactiveSubscriptions(): Flow<List<Subscription>>
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): Subscription?
    @Query("SELECT * FROM subscriptions WHERE category = :category ORDER BY nextPaymentDate ASC")
    fun getSubscriptionsByCategory(category: String): Flow<List<Subscription>>
    @Query("SELECT SUM(amount) FROM subscriptions WHERE isActive = 1 AND billingCycle = 'MONTHLY'")
    fun getMonthlyTotal(): Flow<Double?>
    @Query("""
        SELECT SUM(
            CASE billingCycle
                WHEN 'WEEKLY' THEN amount * 4.33
                WHEN 'MONTHLY' THEN amount
                WHEN 'QUARTERLY' THEN amount / 3.0
                WHEN 'YEARLY' THEN amount / 12.0
                ELSE amount
            END
        ) FROM subscriptions WHERE isActive = 1
    """)
    fun getMonthlyEquivalentTotal(): Flow<Double?>
    @Query("SELECT COUNT(*) FROM subscriptions WHERE isActive = 1")
    fun getActiveSubscriptionCount(): Flow<Int>
    @Query("""
        SELECT * FROM subscriptions 
        WHERE isActive = 1 
        AND nextPaymentDate BETWEEN :startDate AND :endDate 
        ORDER BY nextPaymentDate ASC
    """)
    fun getUpcomingSubscriptions(startDate: Long, endDate: Long): Flow<List<Subscription>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriptions(subscriptions: List<Subscription>)
    @Update
    suspend fun updateSubscription(subscription: Subscription)
    @Delete
    suspend fun deleteSubscription(subscription: Subscription)
    @Query("DELETE FROM subscriptions")
    suspend fun deleteAllSubscriptions()
    @Query("UPDATE subscriptions SET isActive = :isActive WHERE id = :id")
    suspend fun updateSubscriptionStatus(id: Long, isActive: Boolean)
    @Query("UPDATE subscriptions SET nextPaymentDate = :nextDate WHERE id = :id")
    suspend fun updateNextPaymentDate(id: Long, nextDate: Long)
}