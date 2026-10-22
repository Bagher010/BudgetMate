package com.example.myapplication.utils
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
object FormatterCache {
    val currencyFormatter: NumberFormat by lazy { 
        NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }
    val dateFormatter: SimpleDateFormat by lazy { 
        SimpleDateFormat("MMM dd", Locale.getDefault())
    }
    fun formatCurrency(amount: Double): String {
        return try {
            currencyFormatter.format(amount)
        } catch (e: Exception) {
            val formatted = if (amount % 1.0 == 0.0) {
                String.format(Locale.getDefault(), "%.0f", amount)
            } else {
                String.format(Locale.getDefault(), "%.2f", amount)
            }
            "$$formatted"
        }
    }
    fun formatTransactionAmount(amount: Double, isExpense: Boolean): String {
        val prefix = if (isExpense) "-" else "+"
        return try {
            "$prefix${currencyFormatter.format(amount)}"
        } catch (e: Exception) {
            val formatted = if (amount % 1.0 == 0.0) {
                String.format(Locale.getDefault(), "%.0f", amount)
            } else {
                String.format(Locale.getDefault(), "%.2f", amount)
            }
            "$prefix$$formatted"
        }
    }
}