package com.example.myapplication

import com.example.myapplication.ui.viewmodel.BalanceData
import com.example.myapplication.ui.viewmodel.PeriodTotals
import com.example.myapplication.ui.state.BalanceCardUiState
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for balance calculation logic
 */
class BalanceCalculationTest {

    @Test
    fun `balance data calculates correctly with income and expense`() {
        val balanceData = BalanceData(
            balance = 500.0,
            income = 1000.0,
            expense = 500.0
        )
        
        assertEquals(500.0, balanceData.balance, 0.001)
        assertEquals(1000.0, balanceData.income, 0.001)
        assertEquals(500.0, balanceData.expense, 0.001)
    }

    @Test
    fun `balance data with zero values`() {
        val balanceData = BalanceData(
            balance = 0.0,
            income = 0.0,
            expense = 0.0
        )
        
        assertEquals(0.0, balanceData.balance, 0.001)
        assertEquals(0.0, balanceData.income, 0.001)
        assertEquals(0.0, balanceData.expense, 0.001)
    }

    @Test
    fun `balance data with negative balance (expenses exceed income)`() {
        val balanceData = BalanceData(
            balance = -200.0,
            income = 300.0,
            expense = 500.0
        )
        
        assertEquals(-200.0, balanceData.balance, 0.001)
    }

    @Test
    fun `balance data default values are zero`() {
        val balanceData = BalanceData()
        
        assertEquals(0.0, balanceData.balance, 0.001)
        assertEquals(0.0, balanceData.income, 0.001)
        assertEquals(0.0, balanceData.expense, 0.001)
    }

    @Test
    fun `period totals calculates correctly`() {
        val periodTotals = PeriodTotals(
            income = 5000.0,
            expense = 3000.0,
            transactionCount = 25
        )
        
        assertEquals(5000.0, periodTotals.income, 0.001)
        assertEquals(3000.0, periodTotals.expense, 0.001)
        assertEquals(25, periodTotals.transactionCount)
    }

    @Test
    fun `period totals default values are zero`() {
        val periodTotals = PeriodTotals()
        
        assertEquals(0.0, periodTotals.income, 0.001)
        assertEquals(0.0, periodTotals.expense, 0.001)
        assertEquals(0, periodTotals.transactionCount)
    }

    @Test
    fun `balance card ui state from balance data`() {
        val uiState = BalanceCardUiState.from(
            balance = 1500.0,
            income = 2000.0,
            expense = 500.0
        )
        
        // Verify formatted values contain expected amounts (format may vary by locale)
        assertTrue(uiState.formattedBalance.contains("1,500") || uiState.formattedBalance.contains("1500"))
        assertTrue(uiState.formattedIncome.contains("2,000") || uiState.formattedIncome.contains("2000"))
        assertTrue(uiState.formattedExpense.contains("500"))
    }

    @Test
    fun `balance card ui state handles large amounts`() {
        val uiState = BalanceCardUiState.from(
            balance = 1000000.0,
            income = 1500000.0,
            expense = 500000.0
        )
        
        assertNotNull(uiState.formattedBalance)
        assertNotNull(uiState.formattedIncome)
        assertNotNull(uiState.formattedExpense)
    }

    @Test
    fun `balance card ui state handles negative balance`() {
        val uiState = BalanceCardUiState.from(
            balance = -500.0,
            income = 200.0,
            expense = 700.0
        )
        
        assertTrue(uiState.formattedBalance.contains("-") || uiState.formattedBalance.contains("500"))
    }

    @Test
    fun `balance card ui state handles zero values`() {
        val uiState = BalanceCardUiState.from(
            balance = 0.0,
            income = 0.0,
            expense = 0.0
        )
        
        assertNotNull(uiState.formattedBalance)
        assertNotNull(uiState.formattedIncome)
        assertNotNull(uiState.formattedExpense)
    }

    @Test
    fun `income minus expense equals balance`() {
        val income = 1500.0
        val expense = 800.0
        val expectedBalance = income - expense
        
        val balanceData = BalanceData(
            balance = expectedBalance,
            income = income,
            expense = expense
        )
        
        assertEquals(income - expense, balanceData.balance, 0.001)
    }

    @Test
    fun `balance data with decimal precision`() {
        val balanceData = BalanceData(
            balance = 123.45,
            income = 200.99,
            expense = 77.54
        )
        
        assertEquals(123.45, balanceData.balance, 0.001)
        assertEquals(200.99, balanceData.income, 0.001)
        assertEquals(77.54, balanceData.expense, 0.001)
    }
}
