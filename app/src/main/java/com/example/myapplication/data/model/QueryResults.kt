package com.example.myapplication.data.model
import androidx.compose.runtime.Stable
@Stable
data class CategoryTotalResult(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val total: Double,
    val transactionCount: Int
)
@Stable
data class MonthlySummaryResult(
    val month: String, 
    val type: TransactionType,
    val total: Double,
    val count: Int,
    val average: Double
)
@Stable
data class TransactionListItem(
    val id: Long,
    val amount: Double,
    val description: String,
    val date: java.util.Date,
    val type: TransactionType,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long
)
@Stable
data class BalanceSummaryResult(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)