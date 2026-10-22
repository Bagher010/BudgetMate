package com.example.myapplication.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.model.TransactionWithCategory
import com.example.myapplication.ui.theme.incomeColor
import com.example.myapplication.ui.theme.expenseColor
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import com.example.myapplication.ui.viewmodel.SortOrder
import com.example.myapplication.ui.viewmodel.TransactionTypeFilter
import com.example.myapplication.utils.FormatterCache
import com.example.myapplication.utils.getCategoryIcon
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import com.example.myapplication.ui.components.UndoDeleteSnackbarHost
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.concurrent.TimeUnit
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import android.content.ContentValues
import android.provider.MediaStore
import android.os.Build
import java.io.OutputStream
import android.os.Environment
import java.io.FileOutputStream
import java.text.NumberFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
private fun calculateRelativeTime(date: Date): String {
    val diffInMillis = System.currentTimeMillis() - date.time
    val diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
    return when {
        diffInHours < 1 -> "Just now"
        diffInHours < 24 -> "${diffInHours}h ago"
        diffInDays == 1L -> "Yesterday"
        diffInDays < 7 -> "${diffInDays}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}
@Composable
fun OptimizedTransactionCard(
    transactionWithCategory: TransactionWithCategory,
    modifier: Modifier = Modifier
) {
    val relativeTime = remember(transactionWithCategory.transaction.date) {
        calculateRelativeTime(transactionWithCategory.transaction.date)
    }
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            draggedElevation = 0.dp,
            disabledElevation = 0.dp
        )
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
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(transactionWithCategory.category.name),
                    contentDescription = transactionWithCategory.category.name,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transactionWithCategory.transaction.description,
                    style = MaterialTheme.typography.bodyLarge, 
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "${transactionWithCategory.category.name} • $relativeTime",
                    style = MaterialTheme.typography.labelMedium, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Text(
                text = FormatterCache.formatTransactionAmount(
                    transactionWithCategory.transaction.amount,
                    transactionWithCategory.transaction.type == TransactionType.EXPENSE
                ),
                style = MaterialTheme.typography.bodyLarge, 
                fontWeight = FontWeight.Bold, 
                color = if (transactionWithCategory.transaction.type == TransactionType.EXPENSE) {
                    expenseColor()
                } else {
                    incomeColor()
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TransactionsScreen(
    viewModel: FinanceViewModel,
    @Suppress("UNUSED_PARAMETER") onNavigateToAddTransaction: () -> Unit,
    onScrollDirectionChange: (Boolean) -> Unit = {},
    onEditTransaction: (Long) -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val allTransactions by viewModel.transactions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    val allCategories by viewModel.categories.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val lines = reader.readLines()
                reader.close()
                if (lines.isNotEmpty()) {
                    var importedCount = 0
                    var skippedCount = 0
                    var duplicateCount = 0
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val headerIndex = lines.indexOfFirst { it.startsWith("Date,Time,Category") }
                    if (headerIndex == -1) {
                        Toast.makeText(context, "Invalid file format", Toast.LENGTH_SHORT).show()
                        return@let
                    }
                    lines.drop(headerIndex + 1).takeWhile { it.isNotEmpty() && !it.startsWith("Summary") }.forEach { line ->
                        val parts = line.split(",")
                        if (parts.size >= 6) {
                            try {
                                val dateStr = parts[0].trim()
                                val timeStr = parts[1].trim()
                                val categoryName = parts[2].trim()
                                val description = parts[3].trim().replace(";", ",")
                                val amount = parts[4].trim().toDoubleOrNull() ?: 0.0
                                val typeStr = parts[5].trim()
                                val type = if (typeStr.equals("INCOME", ignoreCase = true)) 
                                    TransactionType.INCOME else TransactionType.EXPENSE
                                val date = try {
                                    val parsedDate = dateFormat.parse(dateStr)
                                    val parsedTime = timeFormat.parse(timeStr)
                                    if (parsedDate != null && parsedTime != null) {
                                        val calendar = Calendar.getInstance()
                                        calendar.time = parsedDate
                                        val timeCalendar = Calendar.getInstance()
                                        timeCalendar.time = parsedTime
                                        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                                        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                                        calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND))
                                        calendar.time
                                    } else Date()
                                } catch (e: Exception) { Date() }
                                val category = allCategories.find { cat -> 
                                    cat.name.equals(categoryName, ignoreCase = true) && cat.type == type 
                                }
                                if (category != null) {
                                    val isDuplicate = allTransactions.any { existing ->
                                        existing.transaction.amount == amount &&
                                        existing.transaction.type == type &&
                                        existing.transaction.categoryId == category.id &&
                                        existing.transaction.description == description &&
                                        kotlin.math.abs(existing.transaction.date.time - date.time) < 60000 
                                    }
                                    if (!isDuplicate) {
                                        viewModel.addTransaction(
                                            amount = amount,
                                            description = description,
                                            categoryId = category.id,
                                            type = type,
                                            date = date
                                        )
                                        importedCount++
                                    } else {
                                        duplicateCount++
                                    }
                                } else {
                                    skippedCount++
                                }
                            } catch (e: Exception) {
                                skippedCount++
                            }
                        }
                    }
                    val messageParts = mutableListOf<String>()
                    if (importedCount > 0) messageParts.add("Imported $importedCount")
                    if (duplicateCount > 0) messageParts.add("$duplicateCount duplicates")
                    if (skippedCount > 0) messageParts.add("$skippedCount skipped")
                    val message = if (messageParts.isEmpty()) "No transactions to import" 
                        else messageParts.joinToString(", ")
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to import: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    LaunchedEffect(typeFilter, categoryFilter, allCategories) {
        if (categoryFilter != null) {
            val selectedCategory = allCategories.find { it.id == categoryFilter }
            if (selectedCategory != null) {
                val shouldClearFilter = when (typeFilter) {
                    TransactionTypeFilter.INCOME -> selectedCategory.type != TransactionType.INCOME
                    TransactionTypeFilter.EXPENSE -> selectedCategory.type != TransactionType.EXPENSE
                    TransactionTypeFilter.ALL -> false
                }
                if (shouldClearFilter) {
                    viewModel.updateCategoryFilter(null)
                }
            }
        }
    }
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    val shouldExpandFab by remember {
        derivedStateOf {
            scrollState.lastScrolledBackward || !scrollState.canScrollBackward
        }
    }
    LaunchedEffect(shouldExpandFab) {
        onScrollDirectionChange(shouldExpandFab)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp) 
            ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isSearchActive,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 100,
                        easing = LinearEasing
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = LinearEasing
                    )
                )
            ) {
                SearchTopAppBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearchActiveChange = {
                        isSearchActive = it
                        if (!it) viewModel.updateSearchQuery("")
                    },
                    placeholder = "Search transactions..."
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 200,
                        delayMillis = 100,
                        easing = LinearEasing
                    )
                ),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 150,
                        easing = LinearEasing
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 60.dp, bottom = 16.dp), 
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 36.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .offset(y = 4.dp) 
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp), 
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val searchButtonScale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "searchButtonScale"
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(MaterialShapes.Square.toShape())
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    isSearchActive = true
                                }
                                .graphicsLayer {
                                    scaleX = searchButtonScale
                                    scaleY = searchButtonScale
                                }
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(
                                onClick = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    expanded = !expanded
                                },
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
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                offset = DpOffset(x = (-148).dp, y = 8.dp), 
                                modifier = Modifier.widthIn(max = 180.dp) 
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (sortOrder) {
                                                SortOrder.DATE_DESC -> "Sort by Date (Newest)"
                                                SortOrder.DATE_ASC -> "Sort by Date (Oldest)"
                                                else -> "Sort by Date"
                                            }
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.AutoMirrored.Outlined.Sort,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        expanded = false
                                        val newOrder = if (sortOrder == SortOrder.DATE_DESC) SortOrder.DATE_ASC else SortOrder.DATE_DESC
                                        viewModel.updateSortOrder(newOrder)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (sortOrder) {
                                                SortOrder.AMOUNT_DESC -> "Sort by Amount (Highest)"
                                                SortOrder.AMOUNT_ASC -> "Sort by Amount (Lowest)"
                                                else -> "Sort by Amount"
                                            }
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.AttachMoney,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        expanded = false
                                        val newOrder = if (sortOrder == SortOrder.AMOUNT_DESC) SortOrder.AMOUNT_ASC else SortOrder.AMOUNT_DESC
                                        viewModel.updateSortOrder(newOrder)
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Export Data") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.FileDownload,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        expanded = false
                                        exportTransactionsData(
                                            context = context,
                                            transactions = filteredTransactions,
                                            searchQuery = searchQuery,
                                            typeFilter = typeFilter,
                                            categoryFilter = categoryFilter,
                                            allCategories = allCategories
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Import Data") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.FileUpload,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        expanded = false
                                        importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "application/csv"))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 24.dp, 
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                    item {
                        CategoryFilterRow(
                            categories = allCategories,
                            selectedCategoryId = categoryFilter,
                            typeFilter = typeFilter,
                            onCategorySelected = { categoryId ->
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.updateCategoryFilter(categoryId)
                        },
                        onTypeFilterSelected = { filter ->
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.updateTypeFilter(filter)
                            }
                        )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (filteredTransactions.isEmpty()) {
                    item {
                        val isFiltered = searchQuery.isNotEmpty() ||
                                typeFilter != TransactionTypeFilter.ALL ||
                                categoryFilter != null
                            EmptyTransactionsState(
                                isFiltered = isFiltered
                            )
                    }
                } else {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            tonalElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "All Transactions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    items(
                        items = filteredTransactions,
                        key = { it.transaction.id },
                        contentType = { "transaction" }
                    ) { transactionWithCategory ->
                        val onDelete = remember(transactionWithCategory.transaction.id) {
                            {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.deleteTransaction(transactionWithCategory.transaction)
                            }
                        }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem(
                                    fadeInSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    fadeOutSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    placementSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ),
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            tonalElevation = 1.dp
                        ) {
                            SwipeableTransactionCard(
                                transactionWithCategory = transactionWithCategory,
                                onDelete = onDelete,
                                onEdit = { onEditTransaction(transactionWithCategory.transaction.id) }
                            )
                        }
                    }
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
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
        }
        UndoDeleteSnackbarHost(
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SwipeableTransactionCard(
    transactionWithCategory: TransactionWithCategory,
    onDelete: () -> Unit,
    onEdit: () -> Unit = {}
) {
    var isDeleting by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    val hapticFeedback = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.35f }
    )
    LaunchedEffect(Unit) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }
    val swipeProgress by remember {
        derivedStateOf {
            val progress = dismissState.progress
            progress.coerceIn(0f, 1f)
        }
    }
    val slideX by animateFloatAsState(
        targetValue = when {
            isDeleting -> -1200f
            isEditing -> 1200f
            else -> 0f
        },
        animationSpec = tween(
            durationMillis = 250,
            easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        ),
        label = "slideX"
    )
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (isDeleting || isEditing) 0f else 1f,
        animationSpec = tween(
            durationMillis = 150,
            easing = LinearEasing
        ),
        label = "backgroundAlpha"
    )
    val iconScale by animateFloatAsState(
        targetValue = 0.8f + (swipeProgress * 0.4f),  
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "iconScale"
    )
    LaunchedEffect(dismissState.targetValue) {
        when (dismissState.targetValue) {
            SwipeToDismissBoxValue.EndToStart,
            SwipeToDismissBoxValue.StartToEnd -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            else -> {}
        }
    }
    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                isDeleting = true
                delay(150)
                onDelete()
            }
            SwipeToDismissBoxValue.StartToEnd -> {
                if (!isEditing) {
                    isEditing = true
                    delay(150)
                    onEdit()
                }
            }
            else -> {}
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val isSwipingToDelete = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
            val isSwipingToEdit = dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .alpha(backgroundAlpha)
            ) {
                if (isSwipingToEdit || dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = if (isSwipingToEdit) iconScale else 0.8f
                                    scaleY = if (isSwipingToEdit) iconScale else 0.8f
                                    alpha = if (isSwipingToEdit) 1f else 0.5f
                                },
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
                if (isSwipingToDelete || dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = if (isSwipingToDelete) iconScale else 0.8f
                                    scaleY = if (isSwipingToDelete) iconScale else 0.8f
                                    alpha = if (isSwipingToDelete) 1f else 0.5f
                                },
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .graphicsLayer {
                    translationX = slideX
                }
        ) {
            OptimizedTransactionCard(
                transactionWithCategory = transactionWithCategory
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    placeholder: String
) {
    val hapticFeedback = LocalHapticFeedback.current
    val searchFieldAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 200,
            delayMillis = 100,
            easing = LinearEasing
        ),
        label = "searchFieldAlpha"
    )
    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text(placeholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(searchFieldAlpha),
                singleLine = true,
                trailingIcon = {
                    AnimatedVisibility(
                        visible = searchQuery.isNotEmpty(),
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 150,
                                easing = LinearEasing
                            )
                        ),
                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = 100,
                                easing = LinearEasing
                            )
                        )
                    ) {
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSearchQueryChange("")
                            },
                            modifier = Modifier.size(48.dp) 
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.size(20.dp) 
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSearchActiveChange(false)
                },
                modifier = Modifier.size(48.dp) 
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(20.dp) 
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
@Composable
fun EmptyTransactionsState(
    isFiltered: Boolean
) {
    com.example.myapplication.ui.components.EmptyStateCard(
        icon = if (isFiltered) Icons.Default.Search else Icons.Filled.Receipt,
        title = if (isFiltered) "No matching transactions" else "No transactions yet",
        description = if (isFiltered)
                "Try adjusting your search or filter criteria"
            else
            "Use the + button below to add your first transaction"
        )
}
@Composable
fun CategoryFilterRow(
    categories: ImmutableList<Category>,
    selectedCategoryId: Long?,
    typeFilter: TransactionTypeFilter,
    onCategorySelected: (Long?) -> Unit,
    onTypeFilterSelected: (TransactionTypeFilter) -> Unit
) {
    val filteredCategories = remember(categories, typeFilter) {
        when (typeFilter) {
            TransactionTypeFilter.ALL -> categories
            TransactionTypeFilter.INCOME -> categories.filter { it.type == TransactionType.INCOME }
            TransactionTypeFilter.EXPENSE -> categories.filter { it.type == TransactionType.EXPENSE }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TypeFilterChipsWithIndicator(
            typeFilter = typeFilter,
            onTypeFilterSelected = onTypeFilterSelected
        )
        if (filteredCategories.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CompactCategoryChip(
                    label = when (typeFilter) {
                        TransactionTypeFilter.INCOME -> "All Income"
                        TransactionTypeFilter.EXPENSE -> "All Expenses"
                        TransactionTypeFilter.ALL -> "All Categories"
                    },
                    isSelected = selectedCategoryId == null,
                    onClick = { onCategorySelected(null) }
                )
                filteredCategories.forEach { category ->
                    CompactCategoryChip(
                        label = category.name,
                        isSelected = selectedCategoryId == category.id,
                        onClick = {
                            onCategorySelected(
                                if (selectedCategoryId == category.id) null else category.id
                            )
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun TypeFilterChipsWithIndicator(
    typeFilter: TransactionTypeFilter,
    onTypeFilterSelected: (TransactionTypeFilter) -> Unit
) {
    val density = LocalDensity.current
    var expenseChipX by remember { mutableFloatStateOf(0f) }
    var expenseChipWidth by remember { mutableFloatStateOf(0f) }
    var balanceChipX by remember { mutableFloatStateOf(0f) }
    var balanceChipWidth by remember { mutableFloatStateOf(0f) }
    var incomeChipX by remember { mutableFloatStateOf(0f) }
    var incomeChipWidth by remember { mutableFloatStateOf(0f) }
    val indicatorOffset by animateDpAsState(
        targetValue = with(density) {
            when (typeFilter) {
                TransactionTypeFilter.EXPENSE -> expenseChipX.toDp()
                TransactionTypeFilter.ALL -> balanceChipX.toDp()
                TransactionTypeFilter.INCOME -> incomeChipX.toDp()
            }
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorOffset"
    )
    val indicatorWidth by animateDpAsState(
        targetValue = with(density) {
            when (typeFilter) {
                TransactionTypeFilter.EXPENSE -> expenseChipWidth.toDp()
                TransactionTypeFilter.ALL -> balanceChipWidth.toDp()
                TransactionTypeFilter.INCOME -> incomeChipWidth.toDp()
            }
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorWidth"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .height(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CompactTypeFilterChip(
                label = "Expense",
                icon = Icons.Filled.ArrowUpward,
                iconInCircle = true,
                isSelected = typeFilter == TransactionTypeFilter.EXPENSE,
                onClick = { onTypeFilterSelected(TransactionTypeFilter.EXPENSE) },
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        expenseChipX = coordinates.positionInParent().x
                        expenseChipWidth = coordinates.size.width.toFloat()
                    },
                showBackground = false
            )
            CompactTypeFilterChip(
                label = "Balance",
                icon = Icons.Filled.Menu,
                isSelected = typeFilter == TransactionTypeFilter.ALL,
                onClick = { onTypeFilterSelected(TransactionTypeFilter.ALL) },
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        balanceChipX = coordinates.positionInParent().x
                        balanceChipWidth = coordinates.size.width.toFloat()
                    },
                showBackground = false
            )
            CompactTypeFilterChip(
                label = "Income",
                icon = Icons.Filled.ArrowDownward,
                iconInCircle = true,
                isSelected = typeFilter == TransactionTypeFilter.INCOME,
                onClick = { onTypeFilterSelected(TransactionTypeFilter.INCOME) },
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        incomeChipX = coordinates.positionInParent().x
                        incomeChipWidth = coordinates.size.width.toFloat()
                    },
                showBackground = false
            )
        }
    }
}
@Composable
fun CompactTypeFilterChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconInCircle: Boolean = false,
    showBackground: Boolean = true
) {
    val backgroundColor = if (showBackground && isSelected) 
        MaterialTheme.colorScheme.primaryContainer
    else 
        Color.Transparent
    val textColor = if (isSelected) 
        MaterialTheme.colorScheme.onPrimaryContainer
    else 
        MaterialTheme.colorScheme.onSurfaceVariant
    val iconColor = if (isSelected) 
        MaterialTheme.colorScheme.onPrimaryContainer
    else 
        MaterialTheme.colorScheme.onSurfaceVariant
    val circleBackgroundColor = if (isSelected) 
        MaterialTheme.colorScheme.onPrimaryContainer
    else 
        MaterialTheme.colorScheme.onSurfaceVariant
    val circleIconColor = if (isSelected) 
        MaterialTheme.colorScheme.primaryContainer
    else 
        MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconInCircle) {
                Box(
                    modifier = Modifier.size(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                Box(
                    modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = circleBackgroundColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(9.dp),
                            tint = circleIconColor
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(14.dp),
                    tint = iconColor
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}
@Composable
fun CompactCategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected)
        MaterialTheme.colorScheme.primaryContainer
    else
        Color.Transparent
    val textColor = if (isSelected)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
private fun exportTransactionsData(
    context: Context,
    transactions: ImmutableList<TransactionWithCategory>,
    searchQuery: String,
    typeFilter: TransactionTypeFilter,
    categoryFilter: Long?,
    allCategories: ImmutableList<Category>
) {
    try {
        val fileName = "BudgetMate_export_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
        val currencyFormatter = NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
        val csvContent = StringBuilder()
        csvContent.append("Transactions Export\n")
        csvContent.append("Export Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n")
        if (searchQuery.isNotEmpty()) {
            csvContent.append("Search Query: $searchQuery\n")
        }
        val typeFilterText = when (typeFilter) {
            TransactionTypeFilter.ALL -> "All Transactions"
            TransactionTypeFilter.INCOME -> "Income Only"
            TransactionTypeFilter.EXPENSE -> "Expense Only"
        }
        csvContent.append("Type Filter: $typeFilterText\n")
        if (categoryFilter != null) {
            val categoryName = allCategories.find { it.id == categoryFilter }?.name ?: "Unknown"
            csvContent.append("Category Filter: $categoryName\n")
        }
        csvContent.append("Total Transactions: ${transactions.size}\n")
        csvContent.append("\n")
        csvContent.append("Date,Time,Category,Description,Amount,Type\n")
        transactions.forEach { txn ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(txn.transaction.date)
            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(txn.transaction.date)
            val description = txn.transaction.description.replace(",", ";").replace("\n", " ")
            val amount = txn.transaction.amount
            val type = txn.transaction.type.toString()
            csvContent.append("$dateStr,$timeStr,${txn.category.name},$description,$amount,$type\n")
        }
        csvContent.append("\n")
        csvContent.append("Summary\n")
        val totalIncome = transactions.filter { it.transaction.type == TransactionType.INCOME }.sumOf { it.transaction.amount }
        val totalExpense = transactions.filter { it.transaction.type == TransactionType.EXPENSE }.sumOf { it.transaction.amount }
        val balance = totalIncome - totalExpense
        csvContent.append("Total Income,${currencyFormatter.format(totalIncome)}\n")
        csvContent.append("Total Expense,${currencyFormatter.format(totalExpense)}\n")
        csvContent.append("Balance,${currencyFormatter.format(balance)}\n")
        csvContent.append("\n")
        csvContent.append("Category Breakdown\n")
        csvContent.append("Category,Type,Count,Total Amount\n")
        val categoryGroups = transactions.groupBy { it.category }
        categoryGroups.forEach { (category, categoryTransactions) ->
            val count = categoryTransactions.size
            val total = categoryTransactions.sumOf { it.transaction.amount }
            csvContent.append("${category.name},${category.type},${count},${currencyFormatter.format(total)}\n")
        }
        val outputStream: OutputStream?
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val insertUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            outputStream = insertUri?.let { resolver.openOutputStream(it) }
            insertUri
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            outputStream = FileOutputStream(file)
            FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", file)
        }
        outputStream?.use { stream ->
            stream.write(csvContent.toString().toByteArray())
        }
        android.widget.Toast.makeText(
            context, 
            "Transactions exported to Downloads/$fileName", 
            android.widget.Toast.LENGTH_LONG
        ).show()
        uri?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, it)
                putExtra(Intent.EXTRA_SUBJECT, "Transactions Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Transactions Export"))
        }
    } catch (e: Exception) {
        android.util.Log.e("TransactionsExport", "Error exporting transactions", e)
        android.widget.Toast.makeText(context, "Failed to export transactions: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
    }
}