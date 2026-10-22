package com.example.myapplication.notification
import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _dailyReminderEnabled = MutableStateFlow(prefs.getBoolean(KEY_DAILY_REMINDER, true))
    val dailyReminderEnabled: StateFlow<Boolean> = _dailyReminderEnabled.asStateFlow()
    private val _subscriptionReminderEnabled = MutableStateFlow(prefs.getBoolean(KEY_SUBSCRIPTION_REMINDER, true))
    val subscriptionReminderEnabled: StateFlow<Boolean> = _subscriptionReminderEnabled.asStateFlow()
    private val _transactionAlertsEnabled = MutableStateFlow(prefs.getBoolean(KEY_TRANSACTION_ALERTS, true))
    val transactionAlertsEnabled: StateFlow<Boolean> = _transactionAlertsEnabled.asStateFlow()
    private val _budgetWarningsEnabled = MutableStateFlow(prefs.getBoolean(KEY_BUDGET_WARNINGS, true))
    val budgetWarningsEnabled: StateFlow<Boolean> = _budgetWarningsEnabled.asStateFlow()
    private val _weeklyReportEnabled = MutableStateFlow(prefs.getBoolean(KEY_WEEKLY_REPORT, false))
    val weeklyReportEnabled: StateFlow<Boolean> = _weeklyReportEnabled.asStateFlow()
    private val _monthlyReportEnabled = MutableStateFlow(prefs.getBoolean(KEY_MONTHLY_REPORT, true))
    val monthlyReportEnabled: StateFlow<Boolean> = _monthlyReportEnabled.asStateFlow()
    private val _dailyReminderHour = MutableStateFlow(prefs.getInt(KEY_DAILY_REMINDER_HOUR, 20)) 
    val dailyReminderHour: StateFlow<Int> = _dailyReminderHour.asStateFlow()
    private val _dailyReminderMinute = MutableStateFlow(prefs.getInt(KEY_DAILY_REMINDER_MINUTE, 0))
    val dailyReminderMinute: StateFlow<Int> = _dailyReminderMinute.asStateFlow()
    fun isDailyReminderEnabled(): Boolean = prefs.getBoolean(KEY_DAILY_REMINDER, true)
    fun isSubscriptionReminderEnabled(): Boolean = prefs.getBoolean(KEY_SUBSCRIPTION_REMINDER, true)
    fun isTransactionAlertsEnabled(): Boolean = prefs.getBoolean(KEY_TRANSACTION_ALERTS, true)
    fun isBudgetWarningsEnabled(): Boolean = prefs.getBoolean(KEY_BUDGET_WARNINGS, true)
    fun setDailyReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DAILY_REMINDER, enabled).apply()
        _dailyReminderEnabled.value = enabled
    }
    fun setSubscriptionReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SUBSCRIPTION_REMINDER, enabled).apply()
        _subscriptionReminderEnabled.value = enabled
    }
    fun setTransactionAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TRANSACTION_ALERTS, enabled).apply()
        _transactionAlertsEnabled.value = enabled
    }
    fun setBudgetWarningsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BUDGET_WARNINGS, enabled).apply()
        _budgetWarningsEnabled.value = enabled
    }
    fun setWeeklyReportEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WEEKLY_REPORT, enabled).apply()
        _weeklyReportEnabled.value = enabled
    }
    fun setMonthlyReportEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MONTHLY_REPORT, enabled).apply()
        _monthlyReportEnabled.value = enabled
    }
    fun setDailyReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt(KEY_DAILY_REMINDER_HOUR, hour)
            .putInt(KEY_DAILY_REMINDER_MINUTE, minute)
            .apply()
        _dailyReminderHour.value = hour
        _dailyReminderMinute.value = minute
    }
    fun getDailyReminderHour(): Int = prefs.getInt(KEY_DAILY_REMINDER_HOUR, 20)
    fun getDailyReminderMinute(): Int = prefs.getInt(KEY_DAILY_REMINDER_MINUTE, 0)
    fun isWeeklyReportEnabled(): Boolean = prefs.getBoolean(KEY_WEEKLY_REPORT, false)
    fun isMonthlyReportEnabled(): Boolean = prefs.getBoolean(KEY_MONTHLY_REPORT, true)
    companion object {
        private const val PREFS_NAME = "notification_preferences"
        private const val KEY_DAILY_REMINDER = "daily_reminder"
        private const val KEY_SUBSCRIPTION_REMINDER = "subscription_reminder"
        private const val KEY_TRANSACTION_ALERTS = "transaction_alerts"
        private const val KEY_BUDGET_WARNINGS = "budget_warnings"
        private const val KEY_WEEKLY_REPORT = "weekly_report"
        private const val KEY_MONTHLY_REPORT = "monthly_report"
        private const val KEY_DAILY_REMINDER_HOUR = "daily_reminder_hour"
        private const val KEY_DAILY_REMINDER_MINUTE = "daily_reminder_minute"
    }
}
