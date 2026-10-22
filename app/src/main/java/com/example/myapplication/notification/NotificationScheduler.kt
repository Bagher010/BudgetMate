package com.example.myapplication.notification
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

object NotificationScheduler {
    private const val TAG = "NotificationScheduler"
    private const val REQUEST_DAILY_REMINDER = 1001
    private const val REQUEST_SUBSCRIPTION_CHECK = 1002
    private const val REQUEST_WEEKLY_REPORT = 1003
    private const val REQUEST_MONTHLY_REPORT = 1004
    
    fun scheduleDailyReminder(context: Context, hour: Int = 20, minute: Int = 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_DAILY_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        Log.d(TAG, "Scheduling daily reminder for: ${targetTime.time}")
        scheduleInexactAlarm(alarmManager, targetTime.timeInMillis, pendingIntent)
    }
    
    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_DAILY_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled daily reminder")
    }
    
    fun scheduleSubscriptionReminders(context: Context, runImmediately: Boolean = false) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SUBSCRIPTION_CHECK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_SUBSCRIPTION_CHECK,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        Log.d(TAG, "Scheduling subscription check for: ${targetTime.time}")
        scheduleInexactAlarm(alarmManager, targetTime.timeInMillis, pendingIntent)
        if (runImmediately) {
            Log.d(TAG, "Triggering immediate subscription check")
            val immediateIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = NotificationReceiver.ACTION_SUBSCRIPTION_CHECK
                putExtra(NotificationReceiver.EXTRA_IMMEDIATE, true)
            }
            context.sendBroadcast(immediateIntent)
        }
    }
    
    fun cancelSubscriptionReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SUBSCRIPTION_CHECK
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_SUBSCRIPTION_CHECK,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled subscription reminders")
    }
    
    fun scheduleWeeklyReport(context: Context, triggerImmediately: Boolean = false) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_WEEKLY_REPORT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_WEEKLY_REPORT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val targetTime = Calendar.getInstance().apply {
            val currentDayOfWeek = get(Calendar.DAY_OF_WEEK)
            val daysUntilSunday = if (currentDayOfWeek == Calendar.SUNDAY) 7 else (Calendar.SUNDAY + 7 - currentDayOfWeek) % 7
            add(Calendar.DAY_OF_YEAR, if (daysUntilSunday == 0) 7 else daysUntilSunday)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            val now = Calendar.getInstance()
            if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && 
                now.get(Calendar.HOUR_OF_DAY) < 18) {
                timeInMillis = now.timeInMillis
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
        Log.d(TAG, "Scheduling weekly report for: ${targetTime.time}")
        scheduleInexactAlarm(alarmManager, targetTime.timeInMillis, pendingIntent)
        if (triggerImmediately) {
            Log.d(TAG, "Triggering immediate weekly report")
            val immediateIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = NotificationReceiver.ACTION_WEEKLY_REPORT
                putExtra(NotificationReceiver.EXTRA_IMMEDIATE, true)
            }
            context.sendBroadcast(immediateIntent)
        }
    }
    
    fun cancelWeeklyReport(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_WEEKLY_REPORT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_WEEKLY_REPORT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled weekly report")
    }
    
    fun scheduleMonthlyReport(context: Context, triggerImmediately: Boolean = false) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_MONTHLY_REPORT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_MONTHLY_REPORT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val targetTime = Calendar.getInstance().apply {
            val now = Calendar.getInstance()
            if (now.get(Calendar.DAY_OF_MONTH) == 1 && now.get(Calendar.HOUR_OF_DAY) < 10) {
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            } else {
                add(Calendar.MONTH, 1)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 10)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
        Log.d(TAG, "Scheduling monthly report for: ${targetTime.time}")
        scheduleInexactAlarm(alarmManager, targetTime.timeInMillis, pendingIntent)
        if (triggerImmediately) {
            Log.d(TAG, "Triggering immediate monthly report")
            val immediateIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = NotificationReceiver.ACTION_MONTHLY_REPORT
                putExtra(NotificationReceiver.EXTRA_IMMEDIATE, true)
            }
            context.sendBroadcast(immediateIntent)
        }
    }
    
    fun cancelMonthlyReport(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_MONTHLY_REPORT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_MONTHLY_REPORT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "Cancelled monthly report")
    }
    
    fun initializeSchedules(context: Context) {
        val prefs = NotificationPreferences(context)
        if (prefs.isDailyReminderEnabled()) {
            scheduleDailyReminder(
                context,
                prefs.getDailyReminderHour(),
                prefs.getDailyReminderMinute()
            )
        }
        if (prefs.isSubscriptionReminderEnabled()) {
            scheduleSubscriptionReminders(context)
        }
        if (prefs.isWeeklyReportEnabled()) {
            scheduleWeeklyReport(context)
        }
        if (prefs.isMonthlyReportEnabled()) {
            scheduleMonthlyReport(context)
        }
    }
    private fun scheduleInexactAlarm(alarmManager: AlarmManager, triggerTime: Long, pendingIntent: PendingIntent) {
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}
