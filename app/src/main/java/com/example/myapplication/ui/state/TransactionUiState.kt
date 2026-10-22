package com.example.myapplication.ui.state
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.model.TransactionWithCategory
import com.example.myapplication.utils.FormatterCache
@Stable
data class TransactionUiState(
    val id: Long,
    val formattedAmount: String,
    val formattedDate: String,
    val description: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val amountColor: Color,
    val amountPrefix: String,
    val type: TransactionType,
    val originalTransaction: TransactionWithCategory 
) {
    companion object {
        fun from(transactionWithCategory: TransactionWithCategory): TransactionUiState {
            val transaction = transactionWithCategory.transaction
            val category = transactionWithCategory.category
            return TransactionUiState(
                id = transaction.id,
                formattedAmount = FormatterCache.currencyFormatter.format(transaction.amount),
                formattedDate = FormatterCache.dateFormatter.format(transaction.date),
                description = transaction.description,
                categoryName = category.name,
                categoryIcon = category.icon,
                categoryColor = category.color,
                amountColor = Color.Unspecified, 
                amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+",
                type = transaction.type,
                originalTransaction = transactionWithCategory
            )
        }
    }
}
@Stable
data class CategoryBreakdownUiState(
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val formattedAmount: String,
    val percentage: Float,
    val transactionCount: Int
)
@Stable
data class BalanceCardUiState(
    val formattedBalance: String,
    val formattedIncome: String,
    val formattedExpense: String,
    val balanceColor: Color,
    val isPositive: Boolean
) {
    companion object {
        fun from(balance: Double, income: Double, expense: Double): BalanceCardUiState {
            return BalanceCardUiState(
                formattedBalance = FormatterCache.currencyFormatter.format(balance),
                formattedIncome = FormatterCache.currencyFormatter.format(income),
                formattedExpense = FormatterCache.currencyFormatter.format(expense),
                balanceColor = Color.Unspecified, 
                isPositive = balance >= 0
            )
        }
    }
}