package com.example.myapplication.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.notification.NotificationPreferences
import com.example.myapplication.notification.NotificationScheduler
import androidx.compose.foundation.shape.CircleShape
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notificationPrefs = remember { NotificationPreferences(context) }
    val dailyReminder by notificationPrefs.dailyReminderEnabled.collectAsState()
    val subscriptionReminders by notificationPrefs.subscriptionReminderEnabled.collectAsState()
    val weeklyReport by notificationPrefs.weeklyReportEnabled.collectAsState()
    val monthlyReport by notificationPrefs.monthlyReportEnabled.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onNavigateBack,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Normal
                )
            }
        }
            item {
                Text(
                    text = "Reminders",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 4.dp)
                )
            }
            item {
                NotificationToggleItem(
                    icon = Icons.Default.AccessTime,
                    title = "Daily reminder",
                    description = "Remind me to log transactions",
                    checked = dailyReminder,
                    onCheckedChange = { enabled ->
                        notificationPrefs.setDailyReminderEnabled(enabled)
                        if (enabled) {
                            NotificationScheduler.scheduleDailyReminder(
                                context,
                                notificationPrefs.getDailyReminderHour(),
                                notificationPrefs.getDailyReminderMinute()
                            )
                        } else {
                            NotificationScheduler.cancelDailyReminder(context)
                        }
                    }
                )
            }
            item {
                NotificationToggleItem(
                    icon = Icons.Default.CreditCard,
                    title = "Subscription reminders",
                    description = "Notify before subscription renewals",
                    checked = subscriptionReminders,
                    onCheckedChange = { enabled ->
                        notificationPrefs.setSubscriptionReminderEnabled(enabled)
                        if (enabled) {
                            NotificationScheduler.scheduleSubscriptionReminders(context, runImmediately = true)
                        } else {
                            NotificationScheduler.cancelSubscriptionReminders(context)
                        }
                    }
                )
            }
            item {
                Text(
                    text = "Reports",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 4.dp)
                )
            }
            item {
                NotificationToggleItem(
                    icon = Icons.Default.DateRange,
                    title = "Weekly summary",
                    description = "Receive weekly spending summary",
                    checked = weeklyReport,
                    onCheckedChange = { enabled ->
                        notificationPrefs.setWeeklyReportEnabled(enabled)
                        if (enabled) {
                            NotificationScheduler.scheduleWeeklyReport(context, triggerImmediately = true)
                        } else {
                            NotificationScheduler.cancelWeeklyReport(context)
                        }
                    }
                )
            }
            item {
                NotificationToggleItem(
                    icon = Icons.Default.CalendarMonth,
                    title = "Monthly report",
                    description = "Receive monthly financial report",
                    checked = monthlyReport,
                    onCheckedChange = { enabled ->
                        notificationPrefs.setMonthlyReportEnabled(enabled)
                        if (enabled) {
                            NotificationScheduler.scheduleMonthlyReport(context, triggerImmediately = true)
                        } else {
                            NotificationScheduler.cancelMonthlyReport(context)
                        }
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
}
@Composable
private fun NotificationToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
