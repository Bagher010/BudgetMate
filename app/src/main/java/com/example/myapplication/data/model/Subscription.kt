package com.example.myapplication.data.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    val billingCycle: BillingCycle,
    val category: String,
    val startDate: Date,
    val nextPaymentDate: Date,
    val isActive: Boolean = true,
    val reminderEnabled: Boolean = true,
    val reminderDaysBefore: Int = 3,
    val color: String = "#6200EE",
    val description: String = "",
    val iconName: String = "subscription"
)
enum class BillingCycle(val displayName: String, val months: Int) {
    WEEKLY("Weekly", 0),
    MONTHLY("Monthly", 1),
    QUARTERLY("Quarterly", 3),
    YEARLY("Yearly", 12);
    fun getMonthlyEquivalent(amount: Double): Double {
        return when (this) {
            WEEKLY -> amount * 4.33
            MONTHLY -> amount
            QUARTERLY -> amount / 3
            YEARLY -> amount / 12
        }
    }
}