package com.example.myapplication.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.NumberFormat

@Composable
fun IntroductionScreen(
    onGetStarted: () -> Unit
) {
    var entrance by remember { mutableFloatStateOf(0f) }
    var expenseVisible by remember { mutableStateOf(false) }
    var incomeVisible by remember { mutableStateOf(false) }
    var subscriptionVisible by remember { mutableStateOf(false) }
    var badgeVisible by remember { mutableStateOf(false) }
    var copyVisible by remember { mutableStateOf(false) }
    var actionVisible by remember { mutableStateOf(false) }
    val illustrationOffset by animateFloatAsState(
        targetValue = entrance,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 180f),
        label = "introduction illustration entrance"
    )
    val badgeProgress by animateFloatAsState(
        targetValue = if (badgeVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 240f),
        label = "subscription reminder entrance"
    )
    val copyProgress by animateFloatAsState(
        targetValue = if (copyVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 190f),
        label = "introduction copy entrance"
    )
    val actionProgress by animateFloatAsState(
        targetValue = if (actionVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "get started entrance"
    )
    LaunchedEffect(Unit) {
        entrance = 1f
        delay(240)
        expenseVisible = true
        delay(190)
        incomeVisible = true
        delay(190)
        subscriptionVisible = true
        delay(170)
        badgeVisible = true
        delay(180)
        copyVisible = true
        delay(220)
        actionVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.navigationBars))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FinanceHeroIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .graphicsLayer {
                        translationY = (1f - illustrationOffset) * 28.dp.toPx()
                        alpha = illustrationOffset
                        scaleX = 0.94f + illustrationOffset * 0.06f
                        scaleY = 0.94f + illustrationOffset * 0.06f
                    },
                badgeProgress = badgeProgress,
                expenseVisible = expenseVisible,
                incomeVisible = incomeVisible,
                subscriptionVisible = subscriptionVisible
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Know where\nyour money goes",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 40.sp,
                modifier = Modifier.graphicsLayer {
                    alpha = copyProgress
                    translationY = (1f - copyProgress) * 18.dp.toPx()
                }
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Record income and expenses, track recurring subscriptions, and keep shared costs with contacts.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .graphicsLayer {
                        alpha = copyProgress
                        translationY = (1f - copyProgress) * 18.dp.toPx()
                    }
            )

            Spacer(Modifier.height(20.dp))
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .graphicsLayer {
                    alpha = actionProgress
                    translationY = (1f - actionProgress) * 14.dp.toPx()
                },
            enabled = actionProgress > 0.98f,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Get started",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FinanceHeroIllustration(
    modifier: Modifier = Modifier,
    badgeProgress: Float,
    expenseVisible: Boolean,
    incomeVisible: Boolean,
    subscriptionVisible: Boolean
) {
    val currencyFormatter = remember { NumberFormat.getCurrencyInstance() }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Surface(
            modifier = Modifier
                .width(218.dp)
                .padding(top = 14.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Recent activity",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "This month",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                ActivityExampleRow(
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    title = "Groceries",
                    kind = "Expense",
                    amount = "−${currencyFormatter.format(42.50)}",
                    tint = MaterialTheme.colorScheme.primary,
                    visible = expenseVisible
                )
                ActivityExampleRow(
                    icon = Icons.Default.Payments,
                    title = "Paycheck",
                    kind = "Income",
                    amount = "+${currencyFormatter.format(1250.0)}",
                    tint = MaterialTheme.colorScheme.tertiary,
                    visible = incomeVisible
                )
                ActivityExampleRow(
                    icon = Icons.Default.MusicNote,
                    title = "Music plan",
                    kind = "Subscription",
                    amount = "−${currencyFormatter.format(9.99)}",
                    tint = MaterialTheme.colorScheme.secondary,
                    visible = subscriptionVisible
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 14.dp, end = 4.dp)
                .graphicsLayer {
                    alpha = badgeProgress
                    scaleX = 0.82f + badgeProgress * 0.18f
                    scaleY = 0.82f + badgeProgress * 0.18f
                    translationY = (1f - badgeProgress) * 12.dp.toPx()
                },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Subscription due soon",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun ActivityExampleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    kind: String,
    amount: String,
    tint: androidx.compose.ui.graphics.Color,
    visible: Boolean
) {
    val rowProgress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 220f),
        label = "$kind row entrance"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .graphicsLayer {
                alpha = rowProgress
                translationY = (1f - rowProgress) * 12.dp.toPx()
                scaleX = 0.96f + rowProgress * 0.04f
                scaleY = 0.96f + rowProgress * 0.04f
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(12.dp),
            color = tint.copy(alpha = 0.14f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
            Text(
                text = kind,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

