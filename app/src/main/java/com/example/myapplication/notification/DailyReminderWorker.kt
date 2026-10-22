package com.example.myapplication.notification
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.d(TAG, "DailyReminderWorker started")
        val prefs = NotificationPreferences(applicationContext)
        val isEnabled = prefs.isDailyReminderEnabled()
        Log.d(TAG, "Daily reminder enabled: $isEnabled")
        if (isEnabled) {
            Log.d(TAG, "Showing daily reminder notification")
            NotificationHelper.showDailyReminderNotification(applicationContext)
            Log.d(TAG, "Notification shown")
        }
        return Result.success()
    }
    companion object {
        private const val TAG = "DailyReminderWorker"
        const val WORK_NAME = "daily_reminder_work"
    }
}
