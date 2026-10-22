package com.example.myapplication.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.example.myapplication.ui.components.UndoDeleteSnackbarHost
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.example.myapplication.data.model.Contact
import com.example.myapplication.ui.theme.incomeColor
import com.example.myapplication.ui.theme.expenseColor
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: FinanceViewModel,
    onNavigateToAddContact: () -> Unit,
    onNavigateBack: () -> Unit,
    onScrollDirectionChange: (Boolean) -> Unit = {},
    onContactClick: (Long) -> Unit = {},
    onEditContact: (Long) -> Unit = {}
) {
    val contacts by viewModel.contacts.collectAsState()
    val totalOwedToUser by viewModel.totalOwedToUser.collectAsState()
    val totalUserOwes by viewModel.totalUserOwes.collectAsState()
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }
    val hapticFeedback = LocalHapticFeedback.current
    val incomeColor = incomeColor()
    val expenseColor = expenseColor()
    var owesYouExpanded by rememberSaveable { mutableStateOf(true) }
    var youOweExpanded by rememberSaveable { mutableStateOf(true) }
    var settledExpanded by rememberSaveable { mutableStateOf(false) }
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
    val balanceMap = remember { mutableStateMapOf<Long, Double>() }
    LaunchedEffect(contacts) {
        val currentIds = contacts.map { it.id }.toSet()
        balanceMap.keys.filter { it !in currentIds }.forEach { balanceMap.remove(it) }
    }
    contacts.forEach { contact ->
        LaunchedEffect(contact.id) {
            viewModel.getContactBalance(contact.id).collect { balance ->
                balanceMap[contact.id] = balance
            }
        }
    }
    val owesYouContacts by remember {
        derivedStateOf {
            contacts.filter { (balanceMap[it.id] ?: 0.0) > 0 }
        }
    }
    val youOweContacts by remember {
        derivedStateOf {
            contacts.filter { (balanceMap[it.id] ?: 0.0) < 0 }
        }
    }
    val settledContacts by remember {
        derivedStateOf {
            contacts.filter { (balanceMap[it.id] ?: 0.0) == 0.0 }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 120.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
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
                        text = "Contacts",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (contacts.isEmpty()) {
                item {
                    EmptyContactsState()
                }
            } else {
                item {
                    CollapsibleSectionHeader(
                        title = "Owes You",
                        count = owesYouContacts.size,
                        total = currencyFormatter.format(totalOwedToUser),
                        color = incomeColor,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        expanded = owesYouExpanded,
                        onToggle = { 
                            owesYouExpanded = !owesYouExpanded
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
                item(key = "owes_you_content") {
                    AnimatedVisibility(
                        visible = owesYouExpanded,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            owesYouContacts.forEach { contact ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    tonalElevation = 0.dp
                                ) {
                                    SwipeableContactItem(
                                        contact = contact,
                                        balance = balanceMap[contact.id] ?: 0.0,
                                        currencyFormatter = currencyFormatter,
                                        incomeColor = incomeColor,
                                        expenseColor = expenseColor,
                                        onClick = { onContactClick(contact.id) },
                                        onDelete = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.deleteContact(contact)
                                        },
                                        onEdit = { onEditContact(contact.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    CollapsibleSectionHeader(
                        title = "You Owe",
                        count = youOweContacts.size,
                        total = currencyFormatter.format(totalUserOwes),
                        color = expenseColor,
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        expanded = youOweExpanded,
                        onToggle = { 
                            youOweExpanded = !youOweExpanded 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
                item(key = "you_owe_content") {
                    AnimatedVisibility(
                        visible = youOweExpanded,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            youOweContacts.forEach { contact ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    tonalElevation = 0.dp
                                ) {
                                    SwipeableContactItem(
                                        contact = contact,
                                        balance = balanceMap[contact.id] ?: 0.0,
                                        currencyFormatter = currencyFormatter,
                                        incomeColor = incomeColor,
                                        expenseColor = expenseColor,
                                        onClick = { onContactClick(contact.id) },
                                        onDelete = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.deleteContact(contact)
                                        },
                                        onEdit = { onEditContact(contact.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    CollapsibleSectionHeader(
                        title = "Settled",
                        count = settledContacts.size,
                        total = null,
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Filled.CheckCircle,
                        expanded = settledExpanded,
                        onToggle = { 
                            settledExpanded = !settledExpanded 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
                item(key = "settled_content") {
                    AnimatedVisibility(
                        visible = settledExpanded,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeIn(),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) + fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            settledContacts.forEach { contact ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    tonalElevation = 0.dp
                                ) {
                                    SwipeableContactItem(
                                        contact = contact,
                                        balance = balanceMap[contact.id] ?: 0.0,
                                        currencyFormatter = currencyFormatter,
                                        incomeColor = incomeColor,
                                        expenseColor = expenseColor,
                                        onClick = { onContactClick(contact.id) },
                                        onDelete = {
                                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                            viewModel.deleteContact(contact)
                                        },
                                        onEdit = { onEditContact(contact.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Button(
            onClick = onNavigateToAddContact,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add Contact",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        UndoDeleteSnackbarHost(
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
@Composable
private fun CollapsibleSectionHeader(
    title: String,
    count: Int,
    total: String?,
    color: Color,
    icon: ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "rotation"
    )
    Surface(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "$count ${if (count == 1) "person" else "people"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (total != null) {
                Text(
                    total,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Icon(
                Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer { rotationZ = rotationAngle }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SwipeableContactItem(
    contact: Contact,
    balance: Double,
    currencyFormatter: NumberFormat,
    incomeColor: Color,
    expenseColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
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
            dismissState.progress.coerceIn(0f, 1f)
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
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
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
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                if (isSwipingToEdit || dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
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
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
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
            ContactItemCard(
                contact = contact,
                balance = balance,
                currencyFormatter = currencyFormatter,
                incomeColor = incomeColor,
                expenseColor = expenseColor,
                onClick = onClick
            )
        }
    }
}
@Composable
private fun ContactItemCard(
    contact: Contact,
    balance: Double,
    currencyFormatter: NumberFormat,
    incomeColor: Color,
    expenseColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarColor = try {
        Color(contact.avatarColor.toColorInt())
    } catch (_: Exception) {
        MaterialTheme.colorScheme.primary
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                avatarColor,
                                avatarColor.copy(alpha = 0.6f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (contact.phoneNumber.isNotEmpty()) {
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (balance != 0.0) {
                Text(
                    text = currencyFormatter.format(kotlin.math.abs(balance)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (balance > 0) incomeColor else expenseColor
                )
            } else {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Settled",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
}
@Composable
private fun EmptyContactsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-16).dp)
                ) {
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Track Money with Friends",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Add contacts to split bills, track loans,\nand manage shared expenses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureItem(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconColor = incomeColor(),
                title = "Track who owes you",
                description = "Never forget about money lent to friends"
            )
            FeatureItem(
                icon = Icons.AutoMirrored.Filled.TrendingDown,
                iconColor = expenseColor(),
                title = "Remember what you owe",
                description = "Keep track of your debts in one place"
            )
            FeatureItem(
                icon = Icons.Default.CheckCircle,
                iconColor = MaterialTheme.colorScheme.primary,
                title = "Settle up easily",
                description = "Mark transactions as settled when paid"
            )
        }
    }
}
@Composable
private fun FeatureItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = iconColor.copy(alpha = 0.15f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}