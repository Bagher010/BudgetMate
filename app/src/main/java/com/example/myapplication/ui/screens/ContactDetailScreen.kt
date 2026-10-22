package com.example.myapplication.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.myapplication.data.model.Contact
import com.example.myapplication.data.model.ContactTransaction
import com.example.myapplication.data.model.ContactTransactionType
import com.example.myapplication.ui.components.UndoDeleteSnackbarHost
import com.example.myapplication.ui.theme.TransactionColors
import com.example.myapplication.ui.theme.incomeColor
import com.example.myapplication.ui.theme.expenseColor
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contact: Contact,
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val transactions by viewModel.getContactTransactions(contact.id).collectAsState()
    val balance by viewModel.getContactBalance(contact.id).collectAsState()
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var initialTransactionType by remember { mutableStateOf(ContactTransactionType.LENT) }
    var showSettleDialog by remember { mutableStateOf(false) }
    val avatarColor = try {
        Color(contact.avatarColor.toColorInt())
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 120.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
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
                        text = contact.name,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    val hasUnsettledTransactions = transactions.any { !it.isSettled }
                    AnimatedVisibility(visible = hasUnsettledTransactions) {
                        Button(
                            onClick = { showSettleDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Settle All",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            item {
                ContactHeaderCard(
                    contact = contact,
                    balance = balance,
                    avatarColor = avatarColor,
                    currencyFormatter = currencyFormatter
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.ArrowUpward,
                        label = "Lent",
                        color = incomeColor(),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            initialTransactionType = ContactTransactionType.LENT
                            showAddTransactionDialog = true
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Default.ArrowDownward,
                        label = "Borrowed",
                        color = expenseColor(),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            initialTransactionType = ContactTransactionType.BORROWED
                            showAddTransactionDialog = true
                        }
                    )
                    QuickActionButton(
                        icon = Icons.Filled.Receipt,
                        label = "Split",
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            initialTransactionType = ContactTransactionType.BILL_SPLIT
                            showAddTransactionDialog = true
                        }
                    )
                }
            }
            if (transactions.isEmpty()) {
                item {
                    EmptyTransactionsCard()
                }
            } else {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            tonalElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Transaction History",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${transactions.size} items",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        transactions.forEach { transaction ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 1.dp
                            ) {
                                SwipeableContactTransactionCard(
                                    transaction = transaction,
                                    currencyFormatter = currencyFormatter,
                                    dateFormatter = dateFormatter,
                                    onDelete = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.deleteContactTransaction(transaction)
                                    }
                                )
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 4.dp, topEnd = 4.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            tonalElevation = 1.dp
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
        UndoDeleteSnackbarHost(
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    if (showAddTransactionDialog) {
        AddContactTransactionDialog(
            contactName = contact.name,
            initialType = initialTransactionType,
            onDismiss = { 
                showAddTransactionDialog = false
            },
            onConfirm = { amount, description, type ->
                viewModel.addContactTransaction(
                    contactId = contact.id,
                    amount = amount,
                    description = description,
                    type = type
                )
                showAddTransactionDialog = false
            }
        )
    }
    if (showSettleDialog) {
        AlertDialog(
            onDismissRequest = { showSettleDialog = false },
            title = { Text("Settle All Transactions?") },
            text = { 
                Text("This will mark all transactions with ${contact.name} as settled. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.settleContactTransactions(contact.id)
                        showSettleDialog = false
                    }
                ) {
                    Text("Settle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
@Composable
private fun ContactHeaderCard(
    contact: Contact,
    balance: Double,
    avatarColor: Color,
    currencyFormatter: NumberFormat
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(2).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = contact.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (contact.phoneNumber.isNotEmpty()) {
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            val balanceText = when {
                balance > 0 -> "+${currencyFormatter.format(balance)}"
                balance < 0 -> currencyFormatter.format(balance)
                else -> currencyFormatter.format(0.0)
            }
            val balanceColor = when {
                balance > 0 -> incomeColor()
                balance < 0 -> expenseColor()
                else -> MaterialTheme.colorScheme.onPrimaryContainer
            }
            val balanceLabel = when {
                balance > 0 -> "Owes you"
                balance < 0 -> "You owe"
                else -> "Settled up"
            }
            Text(
                text = balanceText,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = balanceColor
            )
            Text(
                text = balanceLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}
@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableContactTransactionCard(
    transaction: ContactTransaction,
    currencyFormatter: NumberFormat,
    dateFormatter: SimpleDateFormat,
    onDelete: () -> Unit
) {
    var isDeleting by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.35f }
    )
    val slideX by animateFloatAsState(
        targetValue = if (isDeleting) -1200f else 0f,
        animationSpec = tween(
            durationMillis = 250,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        ),
        label = "slideX"
    )
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = "backgroundAlpha"
    )
    LaunchedEffect(dismissState.targetValue) {
        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            isDeleting = true
            delay(150)
            onDelete()
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(expenseColor())
                    .alpha(backgroundAlpha)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .graphicsLayer {
                    translationX = slideX
                }
        ) {
            ContactTransactionCard(
                transaction = transaction,
                currencyFormatter = currencyFormatter,
                dateFormatter = dateFormatter
            )
        }
    }
}
@Composable
private fun ContactTransactionCard(
    transaction: ContactTransaction,
    currencyFormatter: NumberFormat,
    dateFormatter: SimpleDateFormat
) {
    val incColor = incomeColor()
    val expColor = expenseColor()
    val (icon, amountPrefix, amountColor) = when (transaction.type) {
        ContactTransactionType.LENT -> Triple(
            Icons.Default.ArrowUpward,
            "+",
            incColor
        )
        ContactTransactionType.BORROWED -> Triple(
            Icons.Default.ArrowDownward,
            "-",
            expColor
        )
        ContactTransactionType.BILL_SPLIT -> Triple(
            Icons.Filled.Receipt,
            if (transaction.amount >= 0) "+" else "",
            if (transaction.amount >= 0) incColor else expColor
        )
        ContactTransactionType.SETTLED -> Triple(
            Icons.Default.CheckCircle,
            "",
            MaterialTheme.colorScheme.tertiary
        )
    }
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
                .background(amountColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = transaction.type.displayName,
                tint = amountColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${transaction.type.displayName} • ${dateFormatter.format(transaction.date)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                if (transaction.isSettled) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = "Settled",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
        Text(
            text = "$amountPrefix${currencyFormatter.format(kotlin.math.abs(transaction.amount))}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (transaction.isSettled) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                amountColor
            }
        )
    }
}
@Composable
private fun EmptyTransactionsCard() {
    com.example.myapplication.ui.components.EmptyStateCard(
        icon = Icons.Filled.Receipt,
        title = "No transactions yet",
        description = "Start tracking by recording a loan or bill split"
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddContactTransactionDialog(
    contactName: String,
    initialType: ContactTransactionType = ContactTransactionType.LENT,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String, type: ContactTransactionType) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember(initialType) { mutableStateOf(initialType) }
    LaunchedEffect(initialType) {
        selectedType = initialType
    }
    val isFormValid = amount.isNotBlank() && 
                      amount.toDoubleOrNull() != null && 
                      amount.toDoubleOrNull()!! > 0 &&
                      description.isNotBlank()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.titleLarge
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "With $contactName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Type",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = listOf(
                            ContactTransactionType.LENT,
                            ContactTransactionType.BORROWED,
                            ContactTransactionType.BILL_SPLIT
                        )
                    ) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedType = type
                            },
                            label = { Text(type.displayName) },
                            leadingIcon = if (selectedType == type) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    prefix = { Text("$") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    placeholder = { 
                        Text(
                            when (selectedType) {
                                ContactTransactionType.LENT -> "e.g., Dinner loan"
                                ContactTransactionType.BORROWED -> "e.g., Movie ticket"
                                ContactTransactionType.BILL_SPLIT -> "e.g., Groceries split"
                                ContactTransactionType.SETTLED -> "Settlement"
                            }
                        )
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onConfirm(
                        amount.toDoubleOrNull() ?: 0.0,
                        description,
                        selectedType
                    )
                },
                enabled = isFormValid
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}