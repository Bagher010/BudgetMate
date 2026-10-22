package com.example.myapplication

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.dao.SubscriptionDao
import com.example.myapplication.data.database.FinanceDatabase
import com.example.myapplication.data.model.BillingCycle
import com.example.myapplication.data.model.Subscription
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
 * Instrumented tests for SubscriptionDao
 * Tests Room database operations for subscriptions
 */
@RunWith(AndroidJUnit4::class)
class SubscriptionDaoTest {

    private lateinit var subscriptionDao: SubscriptionDao
    private lateinit var db: FinanceDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FinanceDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        subscriptionDao = db.subscriptionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertSubscription_andRetrieve() = runBlocking {
        val subscription = Subscription(
            name = "Netflix",
            amount = 15.99,
            description = "Streaming service",
            billingCycle = BillingCycle.MONTHLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Entertainment",
            color = "#E50914",
            iconName = "movie",
            isActive = true
        )
        
        val insertedId = subscriptionDao.insertSubscription(subscription)
        val retrieved = subscriptionDao.getSubscriptionById(insertedId)
        
        assertNotNull(retrieved)
        assertEquals("Netflix", retrieved?.name)
        assertEquals(15.99, retrieved?.amount ?: 0.0, 0.001)
    }

    @Test
    fun getActiveSubscriptions_filtersCorrectly() = runBlocking {
        subscriptionDao.insertSubscription(createTestSubscription("Active 1", isActive = true))
        subscriptionDao.insertSubscription(createTestSubscription("Active 2", isActive = true))
        subscriptionDao.insertSubscription(createTestSubscription("Inactive", isActive = false))
        
        val activeSubscriptions = subscriptionDao.getActiveSubscriptions().first()
        
        assertEquals(2, activeSubscriptions.size)
        assertTrue(activeSubscriptions.all { it.isActive })
    }

    @Test
    fun getInactiveSubscriptions_filtersCorrectly() = runBlocking {
        subscriptionDao.insertSubscription(createTestSubscription("Active", isActive = true))
        subscriptionDao.insertSubscription(createTestSubscription("Inactive 1", isActive = false))
        subscriptionDao.insertSubscription(createTestSubscription("Inactive 2", isActive = false))
        
        val inactiveSubscriptions = subscriptionDao.getInactiveSubscriptions().first()
        
        assertEquals(2, inactiveSubscriptions.size)
        assertTrue(inactiveSubscriptions.none { it.isActive })
    }

    @Test
    fun updateSubscriptionStatus_changesActiveState() = runBlocking {
        val id = subscriptionDao.insertSubscription(createTestSubscription("Test", isActive = true))
        
        subscriptionDao.updateSubscriptionStatus(id, false)
        
        val retrieved = subscriptionDao.getSubscriptionById(id)
        assertFalse(retrieved?.isActive ?: true)
    }

    @Test
    fun deleteSubscription_removesFromDatabase() = runBlocking {
        val subscription = createTestSubscription("To Delete", isActive = true)
        val id = subscriptionDao.insertSubscription(subscription)
        
        subscriptionDao.deleteSubscription(subscription.copy(id = id))
        
        val retrieved = subscriptionDao.getSubscriptionById(id)
        assertNull(retrieved)
    }

    @Test
    fun getActiveSubscriptionCount_countsCorrectly() = runBlocking {
        subscriptionDao.insertSubscription(createTestSubscription("Sub 1", isActive = true))
        subscriptionDao.insertSubscription(createTestSubscription("Sub 2", isActive = true))
        subscriptionDao.insertSubscription(createTestSubscription("Sub 3", isActive = false))
        
        val count = subscriptionDao.getActiveSubscriptionCount().first()
        
        assertEquals(2, count)
    }

    @Test
    fun getSubscriptionsByCategory_filtersCorrectly() = runBlocking {
        subscriptionDao.insertSubscription(createTestSubscription("Netflix", category = "Entertainment"))
        subscriptionDao.insertSubscription(createTestSubscription("Spotify", category = "Entertainment"))
        subscriptionDao.insertSubscription(createTestSubscription("Gym", category = "Health"))
        
        val entertainmentSubs = subscriptionDao.getSubscriptionsByCategory("Entertainment").first()
        
        assertEquals(2, entertainmentSubs.size)
    }

    @Test
    fun updateSubscription_modifiesData() = runBlocking {
        val subscription = createTestSubscription("Original")
        val id = subscriptionDao.insertSubscription(subscription)
        
        val updatedSubscription = subscription.copy(
            id = id,
            name = "Updated",
            amount = 29.99
        )
        subscriptionDao.updateSubscription(updatedSubscription)
        
        val retrieved = subscriptionDao.getSubscriptionById(id)
        assertEquals("Updated", retrieved?.name)
        assertEquals(29.99, retrieved?.amount ?: 0.0, 0.001)
    }

    @Test
    fun getAllSubscriptions_returnsAll() = runBlocking {
        subscriptionDao.insertSubscription(createTestSubscription("Sub 1"))
        subscriptionDao.insertSubscription(createTestSubscription("Sub 2"))
        subscriptionDao.insertSubscription(createTestSubscription("Sub 3"))
        
        val allSubscriptions = subscriptionDao.getAllSubscriptions().first()
        
        assertEquals(3, allSubscriptions.size)
    }

    @Test
    fun subscriptionWithDifferentBillingCycles() = runBlocking {
        subscriptionDao.insertSubscription(createTestSubscription("Weekly", billingCycle = BillingCycle.WEEKLY))
        subscriptionDao.insertSubscription(createTestSubscription("Monthly", billingCycle = BillingCycle.MONTHLY))
        subscriptionDao.insertSubscription(createTestSubscription("Yearly", billingCycle = BillingCycle.YEARLY))
        
        val allSubscriptions = subscriptionDao.getAllSubscriptions().first()
        
        assertEquals(3, allSubscriptions.size)
        assertTrue(allSubscriptions.any { it.billingCycle == BillingCycle.WEEKLY })
        assertTrue(allSubscriptions.any { it.billingCycle == BillingCycle.MONTHLY })
        assertTrue(allSubscriptions.any { it.billingCycle == BillingCycle.YEARLY })
    }

    private fun createTestSubscription(
        name: String,
        isActive: Boolean = true,
        category: String = "Test",
        billingCycle: BillingCycle = BillingCycle.MONTHLY
    ): Subscription {
        return Subscription(
            name = name,
            amount = 10.0,
            description = "Test subscription",
            billingCycle = billingCycle,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = category,
            color = "#000000",
            iconName = "test",
            isActive = isActive
        )
    }
}
