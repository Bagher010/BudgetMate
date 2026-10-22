package com.example.myapplication.ui.screens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import com.example.myapplication.ui.components.UndoDeleteSnackbarHost
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import com.example.myapplication.data.model.BillingCycle
import com.example.myapplication.data.model.Subscription
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Calendar
import java.util.Date
private fun getSubscriptionLogoResId(subscriptionName: String): Int? {
    val normalizedName = subscriptionName.lowercase()
        .replace(" ", "_")
        .replace("+", "plus")
        .replace("&", "and")
        .replace("-", "_")
        .replace("'", "")
    return when (normalizedName) {
        "netflix" -> R.drawable.netflix
        "spotify" -> R.drawable.spotify
        "apple_music" -> R.drawable.applemusic
        "youtube_premium" -> R.drawable.youtube
        "disneyplus" -> R.drawable.disneyplus
        "hbo_max" -> R.drawable.hbomax
        "apple_tv+", "apple_tvplus" -> R.drawable.appletvwhite
        "amazon_prime" -> R.drawable.amazon
        "hulu" -> R.drawable.hulu
        "xbox_game_pass" -> R.drawable.xbox
        "playstation_plus" -> R.drawable.playstation
        "nintendo_online" -> R.drawable.nintendo
        "ea_play" -> R.drawable.ea
        "steam" -> R.drawable.steam
        "microsoft_365" -> R.drawable.microsoft
        "icloudplus", "icloud+" -> R.drawable.icloud
        "google_one" -> R.drawable.googleone
        "dropbox" -> R.drawable.dropbox
        "adobe_creative" -> R.drawable.adobe
        "nordvpn" -> R.drawable.nordvpn
        "github" -> R.drawable.github
        "chatgpt_plus" -> R.drawable.chatgpt
        "notion" -> R.drawable.notion
        "slack" -> R.drawable.slack
        "figma" -> R.drawable.figma
        "medium" -> R.drawable.medium
        "the_new_york_times" -> R.drawable.nytimes
        "substack" -> R.drawable.substack
        "1password" -> R.drawable.onepassword
        "lastpass" -> R.drawable.lastpass
        else -> null
    }
}
private fun getSubscriptionLogoZoom(subscriptionName: String): Pair<Boolean, Float> {
    return when (subscriptionName) {
        "Spotify", "Apple Music", "YouTube Premium", "Disney+", "Figma", "Medium", "Substack" -> Pair(true, 1.2f)
        "Xbox Game Pass", "The New York Times" -> Pair(true, 1.3f)
        "Microsoft 365" -> Pair(true, 0.9f)
        "iCloud+" -> Pair(true, 1.14f)
        "HBO Max", "PlayStation Plus", "Nintendo Online", "EA Play", "Steam", 
        "Netflix", "Amazon Prime", "Hulu", "Dropbox", "Adobe Creative", "NordVPN", "1Password", "LastPass" -> Pair(true, 1.0f)
        else -> Pair(false, 1.0f)
    }
}
private fun getSubscriptionLogoBackgroundColor(subscriptionName: String, defaultColor: Color, surfaceContainerHighest: Color): Color {
    return when (subscriptionName) {
        "Apple TV+" -> Color(0xFF1C1C1E)
        "Microsoft 365" -> Color.White
        "Google One" -> Color.White
        "GitHub" -> Color.White
        "NordVPN" -> Color.White
        "ChatGPT Plus" -> Color.White
        "Notion" -> Color.White
        "Slack" -> Color.White
        "Figma" -> Color.Black
        else -> surfaceContainerHighest
    }
}
private data class SubscriptionComputedData(
    val activeSubscriptions: List<Subscription> = emptyList(),
    val monthlyTotal: Double = 0.0,
    val yearlyTotal: Double = 0.0,
    val categoryBreakdown: List<Pair<String, Double>> = emptyList(),
    val mostExpensiveSubscription: Subscription? = null,
    val averageSubscriptionCost: Double = 0.0,
    val upcomingSubscriptions: List<Subscription> = emptyList(),
    val soonSubscriptions: List<Subscription> = emptyList(),
    val laterSubscriptions: List<Subscription> = emptyList()
)
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SubscriptionScreen(
    viewModel: FinanceViewModel,
    @Suppress("UNUSED_PARAMETER") onNavigateToAddSubscription: () -> Unit,
    onScrollDirectionChange: (Boolean) -> Unit = {},
    onEditSubscription: (Long) -> Unit = {}
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }
    val hapticFeedback = LocalHapticFeedback.current
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
    var insightsExpanded by remember { mutableStateOf(false) }
    val computedData by produceState(
        initialValue = SubscriptionComputedData(),
        subscriptions
    ) {
        value = withContext(Dispatchers.Default) {
            val active = subscriptions.filter { it.isActive }.sortedBy { it.nextPaymentDate }
            val monthly = active.sumOf { it.billingCycle.getMonthlyEquivalent(it.amount) }
            val yearly = monthly * 12
            val breakdown = active.groupBy { it.category }
                .map { (category, subs) ->
                    category to subs.sumOf { it.billingCycle.getMonthlyEquivalent(it.amount) }
                }
                .sortedByDescending { it.second }
            val mostExpensive = active.maxByOrNull { it.billingCycle.getMonthlyEquivalent(it.amount) }
            val average = if (active.isNotEmpty()) monthly / active.size else 0.0
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val upcoming = active.filter { 
                val paymentDay = Calendar.getInstance().apply {
                    time = it.nextPaymentDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val days = ((paymentDay - today) / (1000 * 60 * 60 * 24)).toInt()
                days in 0..7
            }
            val soon = active.filter { 
                val paymentDay = Calendar.getInstance().apply {
                    time = it.nextPaymentDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val days = ((paymentDay - today) / (1000 * 60 * 60 * 24)).toInt()
                days in 8..30
            }
            val later = active.filter { 
                val paymentDay = Calendar.getInstance().apply {
                    time = it.nextPaymentDate
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val days = ((paymentDay - today) / (1000 * 60 * 60 * 24)).toInt()
                days > 30
            }
            SubscriptionComputedData(
                activeSubscriptions = active,
                monthlyTotal = monthly,
                yearlyTotal = yearly,
                categoryBreakdown = breakdown,
                mostExpensiveSubscription = mostExpensive,
                averageSubscriptionCost = average,
                upcomingSubscriptions = upcoming,
                soonSubscriptions = soon,
                laterSubscriptions = later
            )
        }
    }
    val activeSubscriptions: List<Subscription> = computedData.activeSubscriptions
    val monthlyTotal: Double = computedData.monthlyTotal
    val yearlyTotal: Double = computedData.yearlyTotal
    val categoryBreakdown: List<Pair<String, Double>> = computedData.categoryBreakdown
    val mostExpensiveSubscription: Subscription? = computedData.mostExpensiveSubscription
    val averageSubscriptionCost: Double = computedData.averageSubscriptionCost
    val upcomingSubscriptions: List<Subscription> = computedData.upcomingSubscriptions
    val soonSubscriptions: List<Subscription> = computedData.soonSubscriptions
    val laterSubscriptions: List<Subscription> = computedData.laterSubscriptions
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(124.dp)
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 60.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subscriptions",
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
                item(key = "overview_cards") {
                    OverviewCardsSection(
                        monthlyTotal = monthlyTotal,
                        yearlyTotal = yearlyTotal,
                        mostExpensiveSubscription = mostExpensiveSubscription,
                        averageSubscriptionCost = averageSubscriptionCost,
                        categoryBreakdown = categoryBreakdown,
                        currencyFormatter = currencyFormatter,
                        insightsExpanded = insightsExpanded,
                        onInsightsExpandToggle = { insightsExpanded = !insightsExpanded }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (subscriptions.isEmpty()) {
                    item(key = "empty_state") {
                            EmptySubscriptionsState()
                    }
                } else {
                    if (upcomingSubscriptions.isNotEmpty()) {
                        item(key = "upcoming_header") {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "Upcoming",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        items(
                            items = upcomingSubscriptions,
                            key = { it.id },
                            contentType = { "subscription" }
                        ) { subscription ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 1.dp
                            ) {
                                SwipeableSubscriptionCard(
                                    subscription = subscription,
                                    currencyFormatter = currencyFormatter,
                                    onDelete = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.deleteSubscription(subscription)
                                    },
                                    onEdit = { onEditSubscription(subscription.id) }
                                )
                            }
                        }
                        item(key = "upcoming_footer") {
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
                    if (soonSubscriptions.isNotEmpty()) {
                        item(key = "soon_header") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "Soon",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        items(
                            items = soonSubscriptions,
                            key = { it.id },
                            contentType = { "subscription" }
                        ) { subscription ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 1.dp
                            ) {
                                SwipeableSubscriptionCard(
                                    subscription = subscription,
                                    currencyFormatter = currencyFormatter,
                                    onDelete = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.deleteSubscription(subscription)
                                    },
                                    onEdit = { onEditSubscription(subscription.id) }
                                )
                            }
                        }
                        item(key = "soon_footer") {
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
                    if (laterSubscriptions.isNotEmpty()) {
                        item(key = "later_header") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "Later",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        items(
                            items = laterSubscriptions,
                            key = { it.id },
                            contentType = { "subscription" }
                        ) { subscription ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                tonalElevation = 1.dp
                            ) {
                                SwipeableSubscriptionCard(
                                    subscription = subscription,
                                    currencyFormatter = currencyFormatter,
                                    onDelete = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.deleteSubscription(subscription)
                                    },
                                    onEdit = { onEditSubscription(subscription.id) }
                                )
                            }
                        }
                        item(key = "later_footer") {
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
fun SwipeableSubscriptionCard(
    subscription: Subscription,
    currencyFormatter: NumberFormat,
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
            SubscriptionCardContent(
                subscription = subscription,
                currencyFormatter = currencyFormatter
            )
        }
    }
}
@Composable
fun SubscriptionCardContent(
    subscription: Subscription,
    currencyFormatter: NumberFormat
) {
    val daysUntilPayment = remember(subscription.nextPaymentDate) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val paymentDay = Calendar.getInstance().apply {
            time = subscription.nextPaymentDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val days = ((paymentDay - today) / (1000 * 60 * 60 * 24)).toInt()
        days.coerceAtLeast(0)
    }
    val logoResId = remember(subscription.name) { getSubscriptionLogoResId(subscription.name) }
    val (needsZoom, zoomScale) = remember(subscription.name) { getSubscriptionLogoZoom(subscription.name) }
    val surfaceContainerHighest = MaterialTheme.colorScheme.surfaceContainerHighest
    val logoBackgroundColor = remember(subscription.name, logoResId, subscription.color, surfaceContainerHighest) {
        if (logoResId != null) {
            getSubscriptionLogoBackgroundColor(subscription.name, Color(subscription.color.toColorInt()), surfaceContainerHighest)
        } else {
            Color(subscription.color.toColorInt()).copy(alpha = 0.2f)
        }
    }
    val contentScale = remember(subscription.name, needsZoom) {
        when {
            subscription.name == "Google One" -> ContentScale.Fit
            needsZoom -> ContentScale.Crop
            else -> ContentScale.FillBounds
        }
    }
    val imageModifier = remember(needsZoom, zoomScale) {
        Modifier
            .fillMaxSize()
            .then(
                if (needsZoom) {
                    Modifier.padding(0.dp)
                } else {
                    Modifier.padding(2.dp)
                }
            )
            .then(
                if (zoomScale != 1.0f) {
                    Modifier.graphicsLayer {
                        scaleX = zoomScale
                        scaleY = zoomScale
                    }
                } else {
                    Modifier
                }
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
                .background(logoBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            if (logoResId != null) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "${subscription.name} logo",
                    modifier = imageModifier,
                    contentScale = contentScale
                )
            } else {
                Icon(
                    imageVector = getSubscriptionIcon(subscription.iconName),
                    contentDescription = subscription.name,
                    tint = Color(subscription.color.toColorInt()),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = "${subscription.category} • ${
                    when (daysUntilPayment) {
                        0 -> "Due today"
                        1 -> "1 day"
                        else -> "$daysUntilPayment days"
                    }
                }",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = currencyFormatter.format(subscription.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subscription.billingCycle.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
@Composable
fun OverviewCardsSection(
    monthlyTotal: Double,
    yearlyTotal: Double,
    mostExpensiveSubscription: Subscription?,
    averageSubscriptionCost: Double,
    categoryBreakdown: List<Pair<String, Double>>,
    currencyFormatter: NumberFormat,
    insightsExpanded: Boolean,
    onInsightsExpandToggle: () -> Unit
) {
    val animatedMonthly = remember { Animatable(0f) }
    val animatedYearly = remember { Animatable(0f) }
    LaunchedEffect(monthlyTotal, yearlyTotal) {
        animatedMonthly.animateTo(
            targetValue = monthlyTotal.toFloat(),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    LaunchedEffect(yearlyTotal) {
        animatedYearly.animateTo(
            targetValue = yearlyTotal.toFloat(),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Monthly Spending",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currencyFormatter.format(animatedMonthly.value.toDouble()),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Yearly Total",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormatter.format(animatedYearly.value.toDouble()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Average/Sub",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormatter.format(averageSubscriptionCost),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
        Surface(
            onClick = onInsightsExpandToggle,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Spending Insights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Icon(
                        imageVector = if (insightsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (insightsExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AnimatedVisibility(
                    visible = insightsExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Spending by Category",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        categoryBreakdown.take(3).forEach { (category, amount) ->
                            val percentage = if (monthlyTotal > 0) (amount / monthlyTotal * 100).toInt() else 0
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${currencyFormatter.format(amount)} ($percentage%)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                val animatedProgress = remember { Animatable(0f) }
                                LaunchedEffect(percentage) {
                                    animatedProgress.animateTo(
                                        targetValue = percentage / 100f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(animatedProgress.value)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                        if (mostExpensiveSubscription != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Most Expensive",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = mostExpensiveSubscription.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = currencyFormatter.format(
                                        mostExpensiveSubscription.billingCycle.getMonthlyEquivalent(
                                            mostExpensiveSubscription.amount
                                        )
                                    ) + "/mo",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EmptySubscriptionsState() {
    com.example.myapplication.ui.components.EmptyStateCard(
        icon = Icons.Filled.Repeat,
        title = "No subscriptions yet",
        description = "Use the + button below to add your first subscription"
    )
}
fun getSubscriptionIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "netflix", "streaming" -> Icons.Default.LiveTv
        "spotify", "music" -> Icons.Default.MusicNote
        "gym", "fitness" -> Icons.Default.FitnessCenter
        "cloud", "storage" -> Icons.Default.Cloud
        "gaming", "game" -> Icons.Default.SportsEsports
        "shopping" -> Icons.Default.ShoppingBag
        "news", "magazine" -> Icons.AutoMirrored.Filled.Article
        "software", "app" -> Icons.Default.Apps
        "phone", "mobile" -> Icons.Default.Phone
        "internet", "wifi" -> Icons.Default.Wifi
        else -> Icons.Default.Subscriptions
    }
}
fun getSampleSubscriptions(): List<Subscription> {
    val calendar = Calendar.getInstance()
    return listOf(
        Subscription(
            id = 1,
            name = "Apple TV",
            amount = 6.99,
            billingCycle = BillingCycle.MONTHLY,
            category = "Entertainment",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 4)
            }.time,
            color = "#000000",
            iconName = "streaming"
        ),
        Subscription(
            id = 2,
            name = "AppleCare",
            amount = 22.0,
            billingCycle = BillingCycle.MONTHLY,
            category = "Services",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 9)
            }.time,
            color = "#FF0000",
            iconName = "phone"
        ),
        Subscription(
            id = 3,
            name = "CyberGhost",
            amount = 52.99,
            billingCycle = BillingCycle.MONTHLY,
            category = "Security",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 11)
            }.time,
            color = "#FFD700",
            iconName = "software"
        ),
        Subscription(
            id = 4,
            name = "Netflix",
            amount = 15.99,
            billingCycle = BillingCycle.MONTHLY,
            category = "Entertainment",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 13)
            }.time,
            color = "#E50914",
            iconName = "netflix"
        ),
        Subscription(
            id = 5,
            name = "Apple Music",
            amount = 9.99,
            billingCycle = BillingCycle.MONTHLY,
            category = "Entertainment",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 18)
            }.time,
            color = "#FA243C",
            iconName = "music"
        ),
        Subscription(
            id = 6,
            name = "Spotify Premium",
            amount = 9.99,
            billingCycle = BillingCycle.MONTHLY,
            category = "Entertainment",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 12) 
            }.time,
            color = "#1DB954",
            iconName = "spotify"
        ),
        Subscription(
            id = 7,
            name = "Adobe Creative Cloud",
            amount = 52.99,
            billingCycle = BillingCycle.MONTHLY,
            category = "Productivity",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 3) 
            }.time,
            color = "#FF0000",
            iconName = "software"
        ),
        Subscription(
            id = 8,
            name = "iCloud Storage",
            amount = 2.99,
            billingCycle = BillingCycle.MONTHLY,
            category = "Productivity",
            startDate = Date(),
            nextPaymentDate = calendar.apply { 
                time = Date()
                add(Calendar.DAY_OF_MONTH, 18) 
            }.time,
            color = "#007AFF",
            iconName = "cloud"
        )
    )
}