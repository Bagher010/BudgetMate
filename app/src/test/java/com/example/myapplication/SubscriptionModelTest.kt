package com.example.myapplication

import com.example.myapplication.data.model.BillingCycle
import com.example.myapplication.data.model.Subscription
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

/**
 * Unit tests for Subscription data model validation
 */
class SubscriptionModelTest {

    @Test
    fun `subscription creation with valid data succeeds`() {
        val subscription = Subscription(
            id = 1,
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
        
        assertEquals(1L, subscription.id)
        assertEquals("Netflix", subscription.name)
        assertEquals(15.99, subscription.amount, 0.001)
        assertEquals(BillingCycle.MONTHLY, subscription.billingCycle)
        assertTrue(subscription.isActive)
    }

    @Test
    fun `billing cycle WEEKLY is correctly assigned`() {
        val subscription = Subscription(
            id = 1,
            name = "Weekly Service",
            amount = 5.0,
            description = "Weekly subscription",
            billingCycle = BillingCycle.WEEKLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Services",
            color = "#000000",
            iconName = "repeat",
            isActive = true
        )
        
        assertEquals(BillingCycle.WEEKLY, subscription.billingCycle)
    }

    @Test
    fun `billing cycle MONTHLY is correctly assigned`() {
        val subscription = Subscription(
            id = 1,
            name = "Monthly Service",
            amount = 10.0,
            description = "Monthly subscription",
            billingCycle = BillingCycle.MONTHLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Services",
            color = "#000000",
            iconName = "repeat",
            isActive = true
        )
        
        assertEquals(BillingCycle.MONTHLY, subscription.billingCycle)
    }

    @Test
    fun `billing cycle YEARLY is correctly assigned`() {
        val subscription = Subscription(
            id = 1,
            name = "Yearly Service",
            amount = 99.0,
            description = "Yearly subscription",
            billingCycle = BillingCycle.YEARLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Services",
            color = "#000000",
            iconName = "repeat",
            isActive = true
        )
        
        assertEquals(BillingCycle.YEARLY, subscription.billingCycle)
    }

    @Test
    fun `inactive subscription has isActive false`() {
        val subscription = Subscription(
            id = 1,
            name = "Cancelled Service",
            amount = 10.0,
            description = "Cancelled",
            billingCycle = BillingCycle.MONTHLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Services",
            color = "#000000",
            iconName = "cancel",
            isActive = false
        )
        
        assertFalse(subscription.isActive)
    }

    @Test
    fun `subscription with zero amount is valid`() {
        val subscription = Subscription(
            id = 1,
            name = "Free Trial",
            amount = 0.0,
            description = "Free trial period",
            billingCycle = BillingCycle.MONTHLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Trial",
            color = "#000000",
            iconName = "free",
            isActive = true
        )
        
        assertEquals(0.0, subscription.amount, 0.001)
    }

    @Test
    fun `subscription copy creates independent instance`() {
        val original = Subscription(
            id = 1,
            name = "Original",
            amount = 10.0,
            description = "Original",
            billingCycle = BillingCycle.MONTHLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Test",
            color = "#000000",
            iconName = "test",
            isActive = true
        )
        
        val copy = original.copy(amount = 20.0)
        
        assertEquals(10.0, original.amount, 0.001)
        assertEquals(20.0, copy.amount, 0.001)
    }

    @Test
    fun `subscription with special characters in name`() {
        val subscription = Subscription(
            id = 1,
            name = "Disney+ & ESPN",
            amount = 19.99,
            description = "Bundle",
            billingCycle = BillingCycle.MONTHLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Entertainment",
            color = "#000000",
            iconName = "tv",
            isActive = true
        )
        
        assertEquals("Disney+ & ESPN", subscription.name)
    }

    @Test
    fun `subscription auto-generated id defaults to zero`() {
        val subscription = Subscription(
            name = "Auto ID",
            amount = 10.0,
            description = "Auto",
            billingCycle = BillingCycle.MONTHLY,
            startDate = Date(),
            nextPaymentDate = Date(),
            category = "Test",
            color = "#000000",
            iconName = "auto",
            isActive = true
        )
        
        assertEquals(0L, subscription.id)
    }
    
    @Test
    fun `billing cycle monthly equivalent calculation`() {
        val yearlyAmount = 120.0
        val monthlyEquivalent = BillingCycle.YEARLY.getMonthlyEquivalent(yearlyAmount)
        
        assertEquals(10.0, monthlyEquivalent, 0.001)
    }
    
    @Test
    fun `billing cycle quarterly equivalent calculation`() {
        val quarterlyAmount = 30.0
        val monthlyEquivalent = BillingCycle.QUARTERLY.getMonthlyEquivalent(quarterlyAmount)
        
        assertEquals(10.0, monthlyEquivalent, 0.001)
    }
}
