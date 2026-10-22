package com.example.myapplication.ui.screens
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.ui.theme.TransactionColors
import com.example.myapplication.ui.theme.incomeColor
import com.example.myapplication.ui.theme.expenseColor
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*
@Composable
fun rememberDebounced(
    value: String,
    delayMs: Long = 300L
): String {
    var debouncedValue by remember { mutableStateOf(value) }
    LaunchedEffect(value) {
        delay(delayMs)
        debouncedValue = value
    }
    return debouncedValue
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val debouncedAmount = rememberDebounced(amount, 300L)
    val debouncedDescription = rememberDebounced(description, 300L)
    val amountError by remember(debouncedAmount) {
        derivedStateOf {
            when {
                debouncedAmount.isBlank() -> null 
                debouncedAmount.toDoubleOrNull() == null -> "Invalid amount format"
                debouncedAmount.toDoubleOrNull()!! <= 0 -> "Amount must be greater than 0"
                else -> null
            }
        }
    }
    val descriptionError by remember(debouncedDescription) {
        derivedStateOf {
            when {
                debouncedDescription.isBlank() -> null 
                debouncedDescription.length < 3 -> "Description must be at least 3 characters"
                else -> null
            }
        }
    }
    var categoryError by remember { mutableStateOf<String?>(null) }
    val filteredCategories = remember(categories, selectedType) {
        categories.filter { it.type == selectedType }
    }
    fun validateCategory(): Boolean {
        return if (selectedCategory == null) {
            categoryError = "Please select a category"
            false
        } else {
            categoryError = null
            true
        }
    }
    val isFormValid = debouncedAmount.isNotBlank() && 
                     debouncedDescription.isNotBlank() && 
                     selectedCategory != null && 
                     debouncedAmount.toDoubleOrNull() != null && 
                     debouncedAmount.toDoubleOrNull()!! > 0 &&
                     amountError == null &&
                     descriptionError == null &&
                     categoryError == null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
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
                text = "Add Transaction",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Normal
            )
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 1.dp
            ) {
                TransactionTypeToggle(
                    selectedType = selectedType,
                    onTypeSelected = { 
                        selectedType = it
                        selectedCategory = null
                    }
                )
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it }, 
                        label = { Text("Amount") },
                        isError = amountError != null,
                        supportingText = if (amountError != null) {
                            { Text(amountError!!) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        )
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it }, 
                        label = { Text("Description") },
                        isError = descriptionError != null,
                        supportingText = if (descriptionError != null) {
                            { Text(descriptionError!!) }
                        } else null,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            capitalization = KeyboardCapitalization.Sentences 
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                    DateField(
                        selectedDate = selectedDate,
                        onDateClick = { showDatePicker = true }
                    )
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (categoryError != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = categoryError!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (filteredCategories.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredCategories,
                                key = { it.id }
                            ) { category ->
                                val onSelected = remember(category.id) {
                                    {
                                        selectedCategory = category
                                        categoryError = null
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                                CategoryChip(
                                    category = category,
                                    isSelected = selectedCategory?.id == category.id,
                                    onSelected = onSelected
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Loading categories...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val isCategoryValid = validateCategory()
                    if (isFormValid && isCategoryValid) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.addTransaction(
                            amount = debouncedAmount.toDouble(),
                            description = debouncedDescription,
                            categoryId = selectedCategory!!.id,
                            type = selectedType,
                            date = selectedDate
                        )
                        onNavigateBack() 
                    } else {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Transaction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (showDatePicker) {
            DatePickerModal(
                initialDate = selectedDate,
                onDateSelected = { date ->
                    date?.let {
                        selectedDate = Date(it)
                    }
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}
@Composable
fun TransactionTypeToggle(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    var expenseChipX by remember { mutableFloatStateOf(0f) }
    var expenseChipWidth by remember { mutableFloatStateOf(0f) }
    var incomeChipX by remember { mutableFloatStateOf(0f) }
    var incomeChipWidth by remember { mutableFloatStateOf(0f) }
    val indicatorOffset by animateDpAsState(
        targetValue = with(density) {
            when (selectedType) {
                TransactionType.EXPENSE -> expenseChipX.toDp()
                TransactionType.INCOME -> incomeChipX.toDp()
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
            when (selectedType) {
                TransactionType.EXPENSE -> expenseChipWidth.toDp()
                TransactionType.INCOME -> incomeChipWidth.toDp()
            }
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorWidth"
    )
    val indicatorColor = when (selectedType) {
        TransactionType.EXPENSE -> expenseColor()
        TransactionType.INCOME -> incomeColor()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(indicatorColor)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ExpressiveTypeChip(
                label = "Expense",
                icon = Icons.Filled.ArrowUpward,
                isSelected = selectedType == TransactionType.EXPENSE,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTypeSelected(TransactionType.EXPENSE)
                },
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        expenseChipX = coordinates.positionInParent().x
                        expenseChipWidth = coordinates.size.width.toFloat()
                    }
            )
            ExpressiveTypeChip(
                label = "Income",
                icon = Icons.Filled.ArrowDownward,
                isSelected = selectedType == TransactionType.INCOME,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTypeSelected(TransactionType.INCOME)
                },
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        incomeChipX = coordinates.positionInParent().x
                        incomeChipWidth = coordinates.size.width.toFloat()
                    }
            )
        }
    }
}
@Composable
fun ExpressiveTypeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isSelected) 
        Color.White
    else 
        MaterialTheme.colorScheme.onSurfaceVariant
    val circleBackgroundColor = if (isSelected) 
        Color.White.copy(alpha = 0.2f)
    else 
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
    val circleIconColor = if (isSelected) 
        Color.White
    else 
        MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = circleBackgroundColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(14.dp),
                    tint = circleIconColor
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}
@Composable
fun DateField(
    selectedDate: Date,
    onDateClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    OutlinedTextField(
        value = dateFormatter.format(selectedDate),
        onValueChange = { },
        label = { Text("Date") },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDateClick() },
        trailingIcon = {
            IconButton(
                onClick = onDateClick,
                modifier = Modifier.size(48.dp) 
            ) {
                Icon(
                    Icons.Default.DateRange, 
                    contentDescription = "Select Date",
                    modifier = Modifier.size(20.dp) 
                )
            }
        }
    )
}
@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onSelected,
        label = { Text(category.name) },
        leadingIcon = if (isSelected) {
            { 
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialDate: Date,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.time
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
