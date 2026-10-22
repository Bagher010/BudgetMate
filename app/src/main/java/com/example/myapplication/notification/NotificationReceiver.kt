package com.example.myapplication.notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myapplication.data.database.FinanceDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received broadcast: ${intent.action}")
        when (intent.action) {
            ACTION_DAILY_REMINDER -> {
                handleDailyReminder(context)
            }
            ACTION_SUBSCRIPTION_CHECK -> {
                val pendingResult = goAsync()
                val isImmediate = intent.getBooleanExtra(EXTRA_IMMEDIATE, false)
                handleSubscriptionCheck(context, pendingResult, isImmediate)
            }
            ACTION_WEEKLY_REPORT -> {
                val pendingResult = goAsync()
                val isImmediate = intent.getBooleanExtra(EXTRA_IMMEDIATE, false)
                handleWeeklyReport(context, pendingResult, isImmediate)
            }
            ACTION_MONTHLY_REPORT -> {
                val pendingResult = goAsync()
                val isImmediate = intent.getBooleanExtra(EXTRA_IMMEDIATE, false)
                handleMonthlyReport(context, pendingResult, isImmediate)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d(TAG, "Boot completed - rescheduling alarms")
                NotificationScheduler.initializeSchedules(context)
            }
        }
    }
    private fun handleDailyReminder(context: Context) {
        val prefs = NotificationPreferences(context)
        if (prefs.isDailyReminderEnabled()) {
            Log.d(TAG, "Showing daily reminder notification")
            NotificationHelper.showDailyReminderNotification(context)
            NotificationScheduler.scheduleDailyReminder(
                context,
                prefs.getDailyReminderHour(),
                prefs.getDailyReminderMinute()
            )
        }
    }
    private fun handleSubscriptionCheck(context: Context, pendingResult: PendingResult, isImmediate: Boolean) {
        val prefs = NotificationPreferences(context)
        if (prefs.isSubscriptionReminderEnabled()) {
            Log.d(TAG, "Checking subscriptions (immediate: $isImmediate)")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    checkAndNotifySubscriptions(context)
                } finally {
                    if (!isImmediate) {
                        NotificationScheduler.scheduleSubscriptionReminders(context)
                    }
                    pendingResult.finish()
                }
            }
        } else {
            pendingResult.finish()
        }
    }
    private suspend fun checkAndNotifySubscriptions(context: Context) {
        try {
            val database = FinanceDatabase.getDatabase(context)
            val subscriptionDao = database.subscriptionDao()
            val subscriptions = subscriptionDao.getActiveSubscriptions().first()
            val today = Calendar.getInstance()
            Log.d(TAG, "Found ${subscriptions.size} active subscriptions")
            for (subscription in subscriptions) {
                val nextPayment = subscription.nextPaymentDate
                val daysUntilRenewal = getDaysBetween(today.time, nextPayment)
                Log.d(TAG, "Subscription: ${subscription.name}, nextPayment: $nextPayment, daysUntil: $daysUntilRenewal")
                if (daysUntilRenewal in 0..3) {
                    Log.d(TAG, "Showing notification for ${subscription.name}")
                    NotificationHelper.showSubscriptionReminderNotification(
                        context = context,
                        subscriptionId = subscription.id,
                        subscriptionName = subscription.name,
                        amount = subscription.amount,
                        daysUntilRenewal = daysUntilRenewal.toInt()
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking subscriptions", e)
        }
    }
    private fun getDaysBetween(start: Date, end: Date): Long {
        val diffInMillis = end.time - start.time
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
    }
    private fun handleWeeklyReport(context: Context, pendingResult: PendingResult, isImmediate: Boolean = false) {
        val prefs = NotificationPreferences(context)
        if (prefs.isWeeklyReportEnabled()) {
            Log.d(TAG, "Generating weekly report (immediate: $isImmediate)")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = FinanceDatabase.getDatabase(context)
                    val transactionDao = database.transactionDao()
                    val calendar = Calendar.getInstance()
                    val endDate = calendar.time
                    calendar.add(Calendar.DAY_OF_YEAR, -7)
                    val startDate = calendar.time
                    val transactions = transactionDao.getTransactionsByDateRange(startDate, endDate).first()
                    val totalSpent = transactions.filter { it.transaction.type.name == "EXPENSE" }.sumOf { it.transaction.amount }
                    val transactionCount = transactions.size
                    NotificationHelper.showWeeklyReportNotification(
                        context = context,
                        totalSpent = totalSpent,
                        transactionCount = transactionCount
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error generating weekly report", e)
                } finally {
                    if (!isImmediate) {
                        NotificationScheduler.scheduleWeeklyReport(context)
                    }
                    pendingResult.finish()
                }
            }
        } else {
            pendingResult.finish()
        }
    }
    private fun handleMonthlyReport(context: Context, pendingResult: PendingResult, isImmediate: Boolean = false) {
        val prefs = NotificationPreferences(context)
        if (prefs.isMonthlyReportEnabled()) {
            Log.d(TAG, "Generating monthly report (immediate: $isImmediate)")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = FinanceDatabase.getDatabase(context)
                    val transactionDao = database.transactionDao()
                    val calendar = Calendar.getInstance()
                    val monthNames = arrayOf("January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December")
                    val targetMonth: Int
                    val startDate: Date
                    val endDate: Date
                    if (isImmediate) {
                        targetMonth = calendar.get(Calendar.MONTH)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        startDate = calendar.time
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        endDate = calendar.time
                    } else {
                        calendar.add(Calendar.MONTH, -1)
                        targetMonth = calendar.get(Calendar.MONTH)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        startDate = calendar.time
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        endDate = calendar.time
                    }
                    val monthName = monthNames[targetMonth]
                    val transactions = transactionDao.getTransactionsByDateRange(startDate, endDate).first()
                    val totalSpent = transactions.filter { it.transaction.type.name == "EXPENSE" }.sumOf { it.transaction.amount }
                    val totalIncome = transactions.filter { it.transaction.type.name == "INCOME" }.sumOf { it.transaction.amount }
                    val transactionCount = transactions.size
                    NotificationHelper.showMonthlyReportNotification(
                        context = context,
                        totalSpent = totalSpent,
                        totalIncome = totalIncome,
                        transactionCount = transactionCount,
                        monthName = monthName
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error generating monthly report", e)
                } finally {
                    if (!isImmediate) {
                        NotificationScheduler.scheduleMonthlyReport(context)
                    }
                    pendingResult.finish()
                }
            }
        } else {
            pendingResult.finish()
        }
    }
    companion object {
        private const val TAG = "NotificationReceiver"
        const val ACTION_DAILY_REMINDER = "com.example.myapplication.DAILY_REMINDER"
        const val ACTION_SUBSCRIPTION_CHECK = "com.example.myapplication.SUBSCRIPTION_CHECK"
        const val ACTION_WEEKLY_REPORT = "com.example.myapplication.WEEKLY_REPORT"
        const val ACTION_MONTHLY_REPORT = "com.example.myapplication.MONTHLY_REPORT"
        const val EXTRA_IMMEDIATE = "extra_immediate"
    }
}
