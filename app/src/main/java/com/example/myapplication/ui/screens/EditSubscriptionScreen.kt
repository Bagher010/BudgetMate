package com.example.myapplication.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.BillingCycle
import com.example.myapplication.data.model.Subscription
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    subscription: Subscription,
    viewModel: FinanceViewModel,
    onNavigateBack: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    var name by remember { mutableStateOf(subscription.name) }
    var amount by remember { mutableStateOf(subscription.amount.toString()) }
    var description by remember { mutableStateOf(subscription.description) }
    var selectedBillingCycle by remember { mutableStateOf(subscription.billingCycle) }
    var startingDate by remember { mutableStateOf(subscription.startDate) }
    var showDatePicker by remember { mutableStateOf(false) }
    val nameError by remember(name) {
        derivedStateOf {
            when {
                name.isBlank() -> null
                name.length < 2 -> "Name must be at least 2 characters"
                else -> null
            }
        }
    }
    val amountError by remember(amount) {
        derivedStateOf {
            when {
                amount.isBlank() -> null
                amount.toDoubleOrNull() == null -> "Invalid amount format"
                amount.toDoubleOrNull()!! <= 0 -> "Amount must be greater than 0"
                else -> null
            }
        }
    }
    val descriptionError by remember(description) {
        derivedStateOf {
            when {
                description.isBlank() -> null
                description.length < 3 -> "Description must be at least 3 characters"
                else -> null
            }
        }
    }
    val isFormValid by remember(name, amount, nameError, amountError) {
        derivedStateOf {
            name.isNotBlank() &&
            amount.isNotBlank() &&
            amount.toDoubleOrNull() != null &&
            amount.toDoubleOrNull()!! > 0 &&
            nameError == null &&
            amountError == null
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Edit Subscription") },
            navigationIcon = {
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
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
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
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Subscription Name") },
                        isError = nameError != null,
                        supportingText = nameError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.Words
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") },
                        isError = amountError != null,
                        supportingText = amountError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optional)") },
                        isError = descriptionError != null,
                        supportingText = descriptionError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Billing Cycle",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = BillingCycle.entries,
                            key = { it.name }
                        ) { cycle ->
                            FilterChip(
                                selected = selectedBillingCycle == cycle,
                                onClick = { 
                                    selectedBillingCycle = cycle
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                label = { 
                                    Text(
                                        cycle.displayName
                                    ) 
                                },
                                leadingIcon = if (selectedBillingCycle == cycle) {
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
                    }
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    OutlinedTextField(
                        value = dateFormatter.format(startingDate),
                        onValueChange = { },
                        label = { Text("Start Date") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        trailingIcon = {
                            IconButton(
                                onClick = { showDatePicker = true },
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
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    if (isFormValid) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val calendar = Calendar.getInstance().apply {
                            time = startingDate
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val today = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        when (selectedBillingCycle) {
                            BillingCycle.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                            BillingCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                            BillingCycle.QUARTERLY -> calendar.add(Calendar.MONTH, 3)
                            BillingCycle.YEARLY -> calendar.add(Calendar.YEAR, 1)
                        }
                        while (calendar.timeInMillis < today.timeInMillis) {
                            when (selectedBillingCycle) {
                                BillingCycle.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                                BillingCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                                BillingCycle.QUARTERLY -> calendar.add(Calendar.MONTH, 3)
                                BillingCycle.YEARLY -> calendar.add(Calendar.YEAR, 1)
                            }
                        }
                        viewModel.updateSubscription(
                            subscription.copy(
                                name = name,
                                amount = amount.toDouble(),
                                description = description,
                                billingCycle = selectedBillingCycle,
                                startDate = startingDate,
                                nextPaymentDate = calendar.time
                            )
                        )
                        onNavigateBack()
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
                        text = "Save Changes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (showDatePicker) {
            DatePickerModal(
                initialDate = startingDate,
                onDateSelected = { date ->
                    date?.let { startingDate = Date(it) }
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}