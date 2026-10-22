package com.example.myapplication.ui.screens
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.myapplication.ui.components.ConsistentBalanceCard
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.myapplication.ui.components.MoreOptionsBottomSheet
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.SkeletonBalanceCard
import com.example.myapplication.ui.components.SkeletonList
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.model.TransactionWithCategory
import com.example.myapplication.ui.theme.incomeColor
import com.example.myapplication.ui.theme.expenseColor
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import com.example.myapplication.utils.getCategoryIcon
import com.example.myapplication.utils.FormatterCache
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    viewModel: FinanceViewModel,
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToContacts: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onScrollDirectionChange: (Boolean) -> Unit = {}
) {
    val compositionStartTime = remember { System.currentTimeMillis() }
    val transactions by viewModel.transactions.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val totalOwedToUser by viewModel.totalOwedToUser.collectAsState()
    val totalUserOwes by viewModel.totalUserOwes.collectAsState()
    LaunchedEffect(compositionStartTime) {
        val compositionTime = System.currentTimeMillis() - compositionStartTime
        android.util.Log.d("PerformanceMonitor", "🏠 HomeScreen composition: ${compositionTime}ms")
    }
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showMoreOptionsSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(showMoreOptionsSheet) {
        if (showMoreOptionsSheet) {
            sheetState.show()
        }
    }
    val shouldExpandFab by remember {
        derivedStateOf {
            scrollState.lastScrolledBackward || !scrollState.canScrollBackward
        }
    }
    LaunchedEffect(shouldExpandFab) {
        onScrollDirectionChange(shouldExpandFab)
    }
    val verticalSpacing = remember { 2.dp }
    val recentTransactions by remember {
        derivedStateOf { transactions.take(5) }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
        item {
            HomeHeader(
                onMoreOptionsClick = { showMoreOptionsSheet = true }
            )
        }
        item {
            if (isLoading) {
                SkeletonBalanceCard()
            } else {
                OptimizedBalanceCard(
                    balance = balance,
                    income = totalIncome,
                    expense = totalExpense
                )
            }
        }
        item {
            ContactsSummaryCard(
                contactCount = contacts.size,
                totalOwedToUser = totalOwedToUser,
                totalUserOwes = totalUserOwes,
                onClick = onNavigateToContacts
            )
        }
        if (isLoading) {
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 1.dp
            ) {
                TransactionsHeader()
            }
        }
            item {
                SkeletonBalanceCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                SkeletonList(
                    itemCount = 3,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            if (transactions.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 1.dp
                    ) {
                        TransactionsHeader()
                    }
                }
                items(
                    items = recentTransactions,
                    key = { transactionWithCategory: TransactionWithCategory -> transactionWithCategory.transaction.id },
                    contentType = { "transaction" }
                ) { transactionWithCategory: TransactionWithCategory ->
                    FastTransactionItem(transactionWithCategory)
                }
                item {
                    ShowAllButton(
                        onShowAllClick = onNavigateToTransactions
                    )
                }
            }
        }
        if (transactions.isEmpty() && !isLoading) {
            item {
                EmptyStateCard(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
    }
    if (showMoreOptionsSheet) {
        MoreOptionsBottomSheet(
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showMoreOptionsSheet = false
                    }
                }
            },
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToAbout = onNavigateToAbout,
            sheetState = sheetState
        )
    }
}
@Composable
fun OptimizedBalanceCard(
    balance: Double,
    income: Double,
    expense: Double
) {
    // Public source mirror uses a static card placeholder. The animated card
    // implementation is maintained in the private application repository.
    ConsistentBalanceCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = FormatterCache.formatCurrency(balance),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceSummaryItem("Income", FormatterCache.formatCurrency(income))
                BalanceSummaryItem("Expenses", FormatterCache.formatCurrency(expense))
            }
        }
    }
}

@Composable
private fun BalanceSummaryItem(label: String, amount: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
@Composable
fun ContactsSummaryCard(
    contactCount: Int,
    totalOwedToUser: Double,
    totalUserOwes: Double,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Contacts",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Contacts & Splits",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                if (contactCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (totalOwedToUser > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = incomeColor(),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = FormatterCache.formatCurrency(totalOwedToUser),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = incomeColor()
                                )
                            }
                        }
                        if (totalUserOwes > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = expenseColor(),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = FormatterCache.formatCurrency(totalUserOwes),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = expenseColor()
                                )
                            }
                        }
                        if (totalOwedToUser == 0.0 && totalUserOwes == 0.0) {
                            Text(
                                text = if (contactCount == 1) {
                                    "$contactCount contact • All settled"
                                } else {
                                    "$contactCount contacts • All settled"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Track loans & bill splits",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Contacts",
                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
@Composable
fun FastTransactionItem(transactionWithCategory: TransactionWithCategory) {
    val transaction = transactionWithCategory.transaction
    val category = transactionWithCategory.category
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) {
        incomeColor()
    } else {
        expenseColor()
    }
    val amountPrefix = if (isIncome) "+" else "-"
    val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedDate = dateFormatter.format(transaction.date)
    val formattedTime = timeFormatter.format(transaction.date)
    val currentTime = System.currentTimeMillis()
    val transactionTime = transaction.date.time
    val diffInMillis = currentTime - transactionTime
    val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
    val relativeTime = when {
        diffInHours < 1 -> "Just now"
        diffInHours < 24 -> "${diffInHours}h ago"
        diffInDays == 1L -> "Yesterday"
        diffInDays < 7 -> "${diffInDays}d ago"
        else -> formattedDate
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.name),
                    contentDescription = "Category icon for ${category.name}",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .size(24.dp)
                        .semantics { contentDescription = "Category icon for ${category.name}" }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = relativeTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.End
            ) {
            Text(
                    text = "$amountPrefix${FormatterCache.formatCurrency(transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                color = amountColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            }
        }
    }
}
@Composable
fun EmptyStateCard(
    modifier: Modifier = Modifier
) {
    com.example.myapplication.ui.components.EmptyStateCard(
        icon = Icons.Filled.AttachMoney,
        title = "No transactions yet",
        description = "Use the + button below to add your first transaction",
        modifier = modifier
    )
}
@Composable
fun HomeHeader(
    onMoreOptionsClick: () -> Unit = {}
) {
    val titlePadding = remember { 60.dp }
    val bottomPadding = remember { 16.dp }
    val horizontalPadding = remember { 16.dp }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = titlePadding, start = horizontalPadding, end = 12.dp, bottom = bottomPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Home",
            style = MaterialTheme.typography.headlineLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onMoreOptionsClick,
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(MaterialTheme.colorScheme.secondary)
                .size(width = 32.dp, height = 48.dp)
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More Options",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
@Composable
fun TransactionsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent Transactions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
@Composable
fun ShowAllButton(
    onShowAllClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 0.dp),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 4.dp, topEnd = 4.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Button(
            onClick = onShowAllClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Text(
                text = "Show All Transactions",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}