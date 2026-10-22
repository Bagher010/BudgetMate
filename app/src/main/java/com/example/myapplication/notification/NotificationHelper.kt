package com.example.myapplication.notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R

object NotificationHelper {
    const val CHANNEL_DAILY_REMINDER = "daily_reminder"
    const val CHANNEL_SUBSCRIPTION_REMINDER = "subscription_reminder"
    const val CHANNEL_BUDGET_ALERT = "budget_alert"
    const val CHANNEL_TRANSACTION_ALERT = "transaction_alert"
    const val CHANNEL_WEEKLY_REPORT = "weekly_report"
    const val CHANNEL_MONTHLY_REPORT = "monthly_report"
    const val NOTIFICATION_DAILY_REMINDER = 1001
    const val NOTIFICATION_SUBSCRIPTION_BASE = 2000
    const val NOTIFICATION_BUDGET_ALERT = 3001
    const val NOTIFICATION_TRANSACTION_ALERT = 4001
    const val NOTIFICATION_WEEKLY_REPORT = 5001
    const val NOTIFICATION_MONTHLY_REPORT = 6001
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val dailyReminderChannel = NotificationChannel(
                CHANNEL_DAILY_REMINDER,
                "Daily Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to log your transactions"
            }
            val subscriptionChannel = NotificationChannel(
                CHANNEL_SUBSCRIPTION_REMINDER,
                "Subscription Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders before subscription renewals"
            }
            val budgetChannel = NotificationChannel(
                CHANNEL_BUDGET_ALERT,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when approaching budget limits"
            }
            val transactionChannel = NotificationChannel(
                CHANNEL_TRANSACTION_ALERT,
                "Transaction Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts for large transactions"
            }
            val weeklyReportChannel = NotificationChannel(
                CHANNEL_WEEKLY_REPORT,
                "Weekly Reports",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weekly spending summary reports"
            }
            val monthlyReportChannel = NotificationChannel(
                CHANNEL_MONTHLY_REPORT,
                "Monthly Reports",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Monthly financial summary reports"
            }
            notificationManager.createNotificationChannels(
                listOf(dailyReminderChannel, subscriptionChannel, budgetChannel, transactionChannel, weeklyReportChannel, monthlyReportChannel)
            )
        }
    }
    
    fun showDailyReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Time to log your transactions!")
            .setContentText("Don't forget to record today's transactions")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_DAILY_REMINDER, notification)
        } catch (e: SecurityException) {
        }
    }
    
    fun showSubscriptionReminderNotification(
        context: Context,
        subscriptionId: Long,
        subscriptionName: String,
        amount: Double,
        daysUntilRenewal: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "subscriptions")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, subscriptionId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val message = when (daysUntilRenewal) {
            0 -> "$subscriptionName renews today - ${"%.2f".format(amount)}"
            1 -> "$subscriptionName renews tomorrow - ${"%.2f".format(amount)}"
            else -> "$subscriptionName renews in $daysUntilRenewal days - ${"%.2f".format(amount)}"
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_SUBSCRIPTION_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Upcoming Payment")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_SUBSCRIPTION_BASE + subscriptionId.toInt(), 
                notification
            )
        } catch (e: SecurityException) {
        }
    }
    
    fun showBudgetWarningNotification(
        context: Context,
        categoryName: String,
        percentUsed: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "statistics")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_BUDGET_ALERT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Budget Warning")
            .setContentText("You've used $percentUsed% of your $categoryName budget")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_BUDGET_ALERT, notification)
        } catch (e: SecurityException) {
        }
    }

    fun showWeeklyReportNotification(
        context: Context,
        totalSpent: Double,
        transactionCount: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "statistics")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val message = if (transactionCount > 0) {
            "You spent ${"%.2f".format(totalSpent)} across $transactionCount transactions this week"
        } else {
            "No transactions recorded this week. Keep tracking your expenses!"
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_WEEKLY_REPORT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weekly Summary")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_WEEKLY_REPORT, notification)
        } catch (e: SecurityException) {
        }
    }
    
    fun showMonthlyReportNotification(
        context: Context,
        totalSpent: Double,
        totalIncome: Double,
        transactionCount: Int,
        monthName: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "statistics")
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val netBalance = totalIncome - totalSpent
        val balanceText = if (netBalance >= 0) "saved ${"%.2f".format(netBalance)}" else "overspent by ${"%.2f".format(-netBalance)}"
        val message = "$monthName Summary: You $balanceText with $transactionCount transactions"
        val notification = NotificationCompat.Builder(context, CHANNEL_MONTHLY_REPORT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Monthly Financial Report")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_MONTHLY_REPORT, notification)
        } catch (e: SecurityException) {
        }
    }
}
