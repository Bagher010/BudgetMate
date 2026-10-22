package com.example.myapplication.notification
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.data.database.FinanceDatabase
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class SubscriptionReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val prefs = NotificationPreferences(applicationContext)
        if (!prefs.isSubscriptionReminderEnabled()) {
            return Result.success()
        }
        try {
            val database = FinanceDatabase.getDatabase(applicationContext)
            val subscriptionDao = database.subscriptionDao()
            val activeSubscriptions = subscriptionDao.getActiveSubscriptions().first()
            val today = Calendar.getInstance()
            for (subscription in activeSubscriptions) {
                val nextPayment = subscription.nextPaymentDate
                val daysUntilRenewal = getDaysBetween(today.time, nextPayment)
                if (daysUntilRenewal in 0..3) {
                    NotificationHelper.showSubscriptionReminderNotification(
                        context = applicationContext,
                        subscriptionId = subscription.id,
                        subscriptionName = subscription.name,
                        amount = subscription.amount,
                        daysUntilRenewal = daysUntilRenewal.toInt()
                    )
                }
            }
        } catch (e: Exception) {
            return Result.retry()
        }
        return Result.success()
    }
    private fun getDaysBetween(start: Date, end: Date): Long {
        val diffInMillis = end.time - start.time
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
    }
    companion object {
        const val WORK_NAME = "subscription_reminder_work"
    }
}
