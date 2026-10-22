package com.example.myapplication.ui.screens
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import com.example.myapplication.data.model.BillingCycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
data class SubscriptionTemplate(
    val name: String,
    val category: String,
    val icon: ImageVector,
    val color: Color,
    val suggestedPrice: Double,
    val defaultCycle: BillingCycle,
    val description: String,
    val logoResId: Int? = null,
    val needsZoom: Boolean = false,
    val zoomScale: Float = 1.0f
)
private fun getLogoResId(serviceName: String): Int? {
    val normalizedName = serviceName.lowercase()
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
object PopularSubscriptions {
    val streaming by lazy { listOf(
        SubscriptionTemplate(
            name = "Netflix",
            category = "Streaming",
            icon = Icons.Default.PlayArrow,
            color = Color(0xFFE50914),
            suggestedPrice = 15.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Movies & TV Shows",
            logoResId = getLogoResId("Netflix"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "Spotify",
            category = "Music",
            icon = Icons.Default.MusicNote,
            color = Color(0xFF1DB954),
            suggestedPrice = 10.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Music Streaming",
            logoResId = getLogoResId("Spotify"),
            needsZoom = true,
            zoomScale = 1.2f
        ),
        SubscriptionTemplate(
            name = "Apple Music",
            category = "Music",
            icon = Icons.Default.MusicNote,
            color = Color(0xFFFA243C),
            suggestedPrice = 10.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Music streaming",
            logoResId = getLogoResId("Apple Music"),
            needsZoom = true,
            zoomScale = 1.2f
        ),
        SubscriptionTemplate(
            name = "YouTube Premium",
            category = "Streaming",
            icon = Icons.Default.OndemandVideo,
            color = Color(0xFFFF0000),
            suggestedPrice = 13.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Ad-free videos & music",
            logoResId = getLogoResId("YouTube Premium"),
            needsZoom = true,
            zoomScale = 1.2f
        ),
        SubscriptionTemplate(
            name = "Disney+",
            category = "Streaming",
            icon = Icons.Default.Movie,
            color = Color(0xFF113CCF),
            suggestedPrice = 10.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Disney, Marvel, Star Wars",
            logoResId = getLogoResId("Disney+"),
            needsZoom = true,
            zoomScale = 1.2f
        ),
        SubscriptionTemplate(
            name = "HBO Max",
            category = "Streaming",
            icon = Icons.Default.Tv,
            color = Color(0xFF5822B4),
            suggestedPrice = 15.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Premium movies & series",
            logoResId = getLogoResId("HBO Max"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "Apple TV+",
            category = "Streaming",
            icon = Icons.Default.Devices,
            color = Color(0xFF000000),
            suggestedPrice = 9.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Apple Originals",
            logoResId = getLogoResId("Apple TV+")
        ),
        SubscriptionTemplate(
            name = "Amazon Prime",
            category = "Shopping & Streaming",
            icon = Icons.Default.ShoppingCart,
            color = Color(0xFFFF9900),
            suggestedPrice = 14.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Video, Music & Delivery",
            logoResId = getLogoResId("Amazon Prime"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "Hulu",
            category = "Streaming",
            icon = Icons.Default.LiveTv,
            color = Color(0xFF1CE783),
            suggestedPrice = 17.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "TV & Live Sports",
            logoResId = getLogoResId("Hulu"),
            needsZoom = true
        )
    )}
    val gaming by lazy { listOf(
        SubscriptionTemplate(
            name = "Xbox Game Pass",
            category = "Gaming",
            icon = Icons.Default.SportsEsports,
            color = Color(0xFF107C10),
            suggestedPrice = 14.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "100s of games",
            logoResId = getLogoResId("Xbox Game Pass"),
            needsZoom = true,
            zoomScale = 1.3f
        ),
        SubscriptionTemplate(
            name = "PlayStation Plus",
            category = "Gaming",
            icon = Icons.Default.Gamepad,
            color = Color(0xFF003087),
            suggestedPrice = 59.99,
            defaultCycle = BillingCycle.YEARLY,
            description = "Online gaming & free games",
            logoResId = getLogoResId("PlayStation Plus"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "Nintendo Online",
            category = "Gaming",
            icon = Icons.Default.VideogameAsset,
            color = Color(0xFFE60012),
            suggestedPrice = 3.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Online play & classics",
            logoResId = getLogoResId("Nintendo Online"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "EA Play",
            category = "Gaming",
            icon = Icons.Default.Sports,
            color = Color(0xFFFF4747),
            suggestedPrice = 4.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "EA games access",
            logoResId = getLogoResId("EA Play"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "Steam",
            category = "Gaming",
            icon = Icons.Default.SportsEsports,
            color = Color(0xFF1B2838),
            suggestedPrice = 0.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "PC Gaming Platform",
            logoResId = getLogoResId("Steam"),
            needsZoom = true
        )
    )}
    val productivity by lazy { listOf(
        SubscriptionTemplate(
            name = "Microsoft 365",
            category = "Productivity",
            icon = Icons.Default.Description,
            color = Color(0xFF0078D4),
            suggestedPrice = 9.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Office apps & cloud",
            logoResId = getLogoResId("Microsoft 365"),
            needsZoom = true,
            zoomScale = 0.9f
        ),
        SubscriptionTemplate(
            name = "iCloud+",
            category = "Cloud Storage",
            icon = Icons.Default.Cloud,
            color = Color(0xFF3693F3),
            suggestedPrice = 2.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Apple cloud storage",
            logoResId = getLogoResId("iCloud+"),
            needsZoom = true,
            zoomScale = 1.14f
        ),
        SubscriptionTemplate(
            name = "Google One",
            category = "Cloud Storage",
            icon = Icons.Default.CloudQueue,
            color = Color(0xFF4285F4),
            suggestedPrice = 2.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Google storage & VPN",
            logoResId = getLogoResId("Google One")
        ),
        SubscriptionTemplate(
            name = "Dropbox",
            category = "Cloud Storage",
            icon = Icons.Default.Folder,
            color = Color(0xFF0061FF),
            suggestedPrice = 11.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "File storage & sharing",
            logoResId = getLogoResId("Dropbox"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "Adobe Creative",
            category = "Creative",
            icon = Icons.Default.Brush,
            color = Color(0xFFFF0000),
            suggestedPrice = 54.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Creative apps suite",
            logoResId = getLogoResId("Adobe Creative"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "Notion",
            category = "Productivity",
            icon = Icons.AutoMirrored.Filled.Notes,
            color = Color(0xFF000000),
            suggestedPrice = 10.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Notes & project management",
            logoResId = getLogoResId("Notion")
        ),
        SubscriptionTemplate(
            name = "Slack",
            category = "Productivity",
            icon = Icons.Default.Forum,
            color = Color(0xFF4A154B),
            suggestedPrice = 8.75,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Team communication",
            logoResId = getLogoResId("Slack")
        ),
        SubscriptionTemplate(
            name = "Figma",
            category = "Creative",
            icon = Icons.Default.DesignServices,
            color = Color(0xFFF24E1E),
            suggestedPrice = 15.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Design & prototyping",
            logoResId = getLogoResId("Figma"),
            needsZoom = true,
            zoomScale = 1.2f
        )
    )}
    val fitness by lazy { listOf(
        SubscriptionTemplate(
            name = "Gym Membership",
            category = "Fitness",
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFFFF6B35),
            suggestedPrice = 29.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Local gym access"
        )
    )}
    val news by lazy { listOf(
        SubscriptionTemplate(
            name = "News Subscription",
            category = "News",
            icon = Icons.Default.Newspaper,
            color = Color(0xFF1A1A1A),
            suggestedPrice = 9.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Digital news access"
        ),
        SubscriptionTemplate(
            name = "Medium",
            category = "Reading",
            icon = Icons.AutoMirrored.Filled.Article,
            color = Color(0xFF000000),
            suggestedPrice = 5.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Quality articles",
            logoResId = getLogoResId("Medium"),
            needsZoom = true,
            zoomScale = 1.2f
        ),
        SubscriptionTemplate(
            name = "The New York Times",
            category = "News",
            icon = Icons.Default.Newspaper,
            color = Color(0xFF000000),
            suggestedPrice = 17.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "World news coverage",
            logoResId = getLogoResId("The New York Times"),
            needsZoom = true,
            zoomScale = 1.3f
        ),
        SubscriptionTemplate(
            name = "Substack",
            category = "News",
            icon = Icons.Default.Newspaper,
            color = Color(0xFFFF6719),
            suggestedPrice = 5.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Newsletter subscriptions",
            logoResId = getLogoResId("Substack"),
            needsZoom = true,
            zoomScale = 1.2f
        )
    )}
    val utilities by lazy { listOf(
        SubscriptionTemplate(
            name = "NordVPN",
            category = "Security",
            icon = Icons.Default.Security,
            color = Color(0xFF4687FF),
            suggestedPrice = 12.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Privacy & security",
            logoResId = getLogoResId("NordVPN"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "1Password",
            category = "Security",
            icon = Icons.Default.Lock,
            color = Color(0xFF0094F5),
            suggestedPrice = 4.99,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Secure passwords",
            logoResId = getLogoResId("1Password"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "LastPass",
            category = "Security",
            icon = Icons.Default.Lock,
            color = Color(0xFFD32D27),
            suggestedPrice = 3.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Password manager",
            logoResId = getLogoResId("LastPass"),
            needsZoom = true
        ),
        SubscriptionTemplate(
            name = "GitHub",
            category = "Development",
            icon = Icons.Default.Code,
            color = Color(0xFF181717),
            suggestedPrice = 4.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "Code hosting & collaboration",
            logoResId = getLogoResId("GitHub")
        ),
        SubscriptionTemplate(
            name = "ChatGPT Plus",
            category = "AI",
            icon = Icons.Default.SmartToy,
            color = Color(0xFF10A37F),
            suggestedPrice = 20.00,
            defaultCycle = BillingCycle.MONTHLY,
            description = "AI assistant",
            logoResId = getLogoResId("ChatGPT Plus")
        )
    )}
    val all: List<SubscriptionTemplate>
        get() = streaming + gaming + productivity + fitness + news + utilities
    val categories by lazy { listOf(
        "All" to all,
        "Streaming" to streaming,
        "Gaming" to gaming,
        "Productivity" to productivity,
        "Fitness" to fitness,
        "News" to news,
        "Utilities" to utilities
    )}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    onNavigateBack: () -> Unit,
    onSubscriptionAdded: (name: String, amount: Double, description: String, billingCycle: BillingCycle, startDate: Date, category: String, color: String, iconName: String) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }
    val requestNotificationPermission: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedBillingCycle by remember { mutableStateOf(BillingCycle.MONTHLY) }
    var startingDate by remember { mutableStateOf(Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedTemplate by remember { mutableStateOf<SubscriptionTemplate?>(null) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var showCustomForm by remember { mutableStateOf(false) }
    var notificationEnabled by remember { mutableStateOf(true) }
    var notificationDaysBefore by remember { mutableIntStateOf(1) }
    val categories by produceState<List<Pair<String, List<SubscriptionTemplate>>>>(
        initialValue = emptyList()
    ) {
        value = withContext(Dispatchers.Default) {
            PopularSubscriptions.categories
        }
    }
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
    val isFormValid by remember(name, amount, description, nameError, amountError, descriptionError) {
        derivedStateOf {
            name.isNotBlank() &&
            amount.isNotBlank() &&
            description.isNotBlank() &&
            amount.toDoubleOrNull() != null &&
            amount.toDoubleOrNull()!! > 0 &&
            nameError == null &&
            amountError == null &&
            descriptionError == null
        }
    }
    fun selectTemplate(template: SubscriptionTemplate) {
        selectedTemplate = template
        name = template.name
        amount = template.suggestedPrice.toString()
        description = template.description
        selectedBillingCycle = template.defaultCycle
        showCustomForm = true
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = {
                    if (showCustomForm) {
                        showCustomForm = false
                        selectedTemplate = null
                        name = ""
                        amount = ""
                        description = ""
                    } else {
                        onNavigateBack()
                    }
                },
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
                text = if (showCustomForm) "Subscription Details" else "Add Subscription",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Normal
            )
        }
        if (showCustomForm) {
            SubscriptionFormContent(
                    name = name,
                    onNameChange = { name = it },
                    nameError = nameError,
                    amount = amount,
                    onAmountChange = { amount = it },
                    amountError = amountError,
                    description = description,
                    onDescriptionChange = { description = it },
                    descriptionError = descriptionError,
                    selectedBillingCycle = selectedBillingCycle,
                    onBillingCycleChange = { selectedBillingCycle = it },
                    startingDate = startingDate,
                    onDateClick = { showDatePicker = true },
                    isFormValid = isFormValid,
                    selectedTemplate = selectedTemplate,
                    notificationEnabled = notificationEnabled,
                    onNotificationEnabledChange = { notificationEnabled = it },
                    notificationDaysBefore = notificationDaysBefore,
                    onNotificationDaysBeforeChange = { notificationDaysBefore = it },
                    onRequestNotificationPermission = requestNotificationPermission,
                    onSubmit = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val category = selectedTemplate?.category ?: "Subscription"
                        val color = selectedTemplate?.let { template ->
                            val colorInt = android.graphics.Color.argb(
                                (template.color.alpha * 255).toInt(),
                                (template.color.red * 255).toInt(),
                                (template.color.green * 255).toInt(),
                                (template.color.blue * 255).toInt()
                            )
                            String.format(Locale.US, "#%06X", 0xFFFFFF and colorInt)
                        } ?: "#6200EE"
                        val iconName = selectedTemplate?.name?.lowercase()?.replace(" ", "_") ?: "subscription"
                        onSubscriptionAdded(
                            name,
                            amount.toDouble(),
                            description,
                            selectedBillingCycle,
                            startingDate,
                            category,
                            color,
                            iconName
                        )
                        onNavigateBack()
                    },
                    focusManager = focusManager
                )
        } else {
            SubscriptionTemplateSelector(
                categories = categories,
                selectedCategoryIndex = selectedCategoryIndex,
                onCategorySelected = { selectedCategoryIndex = it },
                onTemplateSelected = { selectTemplate(it) },
                onCustomClick = {
                    showCustomForm = true
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        }
        if (showDatePicker) {
            SubscriptionDatePickerDialog(
                initialDate = startingDate,
                onDateSelected = { date ->
                    date?.let {
                        startingDate = Date(it)
                    }
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }
}
@Composable
private fun SubscriptionTemplateSelector(
    categories: List<Pair<String, List<SubscriptionTemplate>>>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit,
    onTemplateSelected: (SubscriptionTemplate) -> Unit,
    onCustomClick: () -> Unit
) {
    if (categories.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(categories.size) { index ->
                val (categoryName, _) = categories[index]
                FilterChip(
                    selected = selectedCategoryIndex == index,
                    onClick = { onCategorySelected(index) },
                    label = { 
                        Text(
                            categoryName,
                            fontWeight = if (selectedCategoryIndex == index) FontWeight.SemiBold else FontWeight.Normal
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Surface(
            onClick = onCustomClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Custom Subscription",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Popular Services",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        val currentSubscriptions = remember(categories, selectedCategoryIndex) {
            derivedStateOf {
                if (categories.isNotEmpty() && selectedCategoryIndex < categories.size) {
                    categories[selectedCategoryIndex].second
                } else {
                    emptyList()
                }
            }
        }.value
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 32.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = currentSubscriptions,
                key = { it.name }
            ) { template ->
                SubscriptionTemplateCard(
                    template = template,
                    onClick = { onTemplateSelected(template) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
@Composable
private fun SubscriptionTemplateCard(
    template: SubscriptionTemplate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceContainerHighest = MaterialTheme.colorScheme.surfaceContainerHighest
    val logoBackgroundColor = remember(template.name, template.logoResId, template.color, surfaceContainerHighest) {
        when {
            template.name == "Apple TV+" -> Color(0xFF1C1C1E)
            template.name == "Microsoft 365" -> Color.White
            template.name == "Google One" -> Color.White
            template.name == "GitHub" -> Color.White
            template.name == "NordVPN" -> Color.White
            template.name == "ChatGPT Plus" -> Color.White
            template.name == "Notion" -> Color.White
            template.name == "Slack" -> Color.White
            template.name == "Figma" -> Color.Black
            template.logoResId != null -> surfaceContainerHighest
            else -> template.color
        }
    }
    val contentScale = remember(template.name, template.needsZoom) {
        when {
            template.name == "Google One" -> ContentScale.Fit
            template.needsZoom -> ContentScale.Crop
            else -> ContentScale.FillBounds
        }
    }
    val imageModifier = remember(template.needsZoom, template.zoomScale) {
        Modifier
            .fillMaxSize()
            .then(
                if (template.needsZoom) {
                    Modifier.padding(0.dp)
                } else {
                    Modifier.padding(2.dp)
                }
            )
            .then(
                if (template.zoomScale != 1.0f) {
                    Modifier.graphicsLayer {
                        scaleX = template.zoomScale
                        scaleY = template.zoomScale
                    }
                } else {
                    Modifier
                }
            )
    }
    Surface(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(logoBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                if (template.logoResId != null) {
                    Image(
                        painter = painterResource(id = template.logoResId),
                        contentDescription = "${template.name} logo",
                        modifier = imageModifier,
                        contentScale = contentScale
                    )
                } else {
                    Icon(
                        template.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = String.format(Locale.US, if (template.suggestedPrice % 1.0 == 0.0) "$%.0f/%s" else "$%.2f/%s", template.suggestedPrice, template.defaultCycle.name.lowercase().take(2)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
private fun SubscriptionFormContent(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    amount: String,
    onAmountChange: (String) -> Unit,
    amountError: String?,
    description: String,
    onDescriptionChange: (String) -> Unit,
    descriptionError: String?,
    selectedBillingCycle: BillingCycle,
    onBillingCycleChange: (BillingCycle) -> Unit,
    startingDate: Date,
    onDateClick: () -> Unit,
    isFormValid: Boolean,
    selectedTemplate: SubscriptionTemplate?,
    onSubmit: () -> Unit,
    focusManager: FocusManager,
    notificationEnabled: Boolean,
    onNotificationEnabledChange: (Boolean) -> Unit,
    notificationDaysBefore: Int,
    onNotificationDaysBeforeChange: (Int) -> Unit,
    onRequestNotificationPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        selectedTemplate?.let { template ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = template.color.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                when {
                                    template.name == "Apple TV+" -> Color(0xFF1C1C1E)
                                    template.name == "Google One" -> Color.White
                                    template.name == "GitHub" -> Color.White
                                    template.name == "NordVPN" -> Color.White
                                    template.name == "ChatGPT Plus" -> Color.White
                                    template.name == "Notion" -> Color.White
                                    template.name == "Slack" -> Color.White
                                    template.name == "Figma" -> Color.Black
                                    template.logoResId != null -> MaterialTheme.colorScheme.surfaceContainerHighest 
                                    else -> template.color
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (template.logoResId != null) {
                            Image(
                                painter = painterResource(id = template.logoResId),
                                contentDescription = "${template.name} logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .then(
                                        if (template.needsZoom) {
                                            Modifier.padding(0.dp)
                                        } else {
                                            Modifier.padding(3.dp)
                                        }
                                    )
                                    .then(
                                        if (template.zoomScale != 1.0f) {
                                            Modifier.graphicsLayer {
                                                scaleX = template.zoomScale
                                                scaleY = template.zoomScale
                                            }
                                        } else {
                                            Modifier
                                        }
                                    ),
                                contentScale = when {
                                    template.name == "Google One" -> ContentScale.Fit
                                    template.needsZoom -> ContentScale.Crop
                                    else -> ContentScale.FillBounds
                                }
                            )
                        } else {
                            Icon(
                                template.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = template.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 1.dp
        ) {
            SubscriptionBillingCycleToggle(
                selectedCycle = selectedBillingCycle,
                onCycleSelected = onBillingCycleChange
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
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Subscription Name") },
                    isError = nameError != null,
                    supportingText = if (nameError != null) {
                        { Text(nameError) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        capitalization = KeyboardCapitalization.Words
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = { Text("Amount") },
                    isError = amountError != null,
                    supportingText = if (amountError != null) {
                        { Text(amountError) }
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
                    ),
                    leadingIcon = {
                        Text(
                            "$",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    isError = descriptionError != null,
                    supportingText = if (descriptionError != null) {
                        { Text(descriptionError) }
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
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                SubscriptionDateField(
                    selectedDate = startingDate,
                    onDateClick = onDateClick
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
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Renewal Reminder",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Get notified before renewal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = notificationEnabled,
                        onCheckedChange = { enabled ->
                            onNotificationEnabledChange(enabled)
                            if (enabled) {
                                onRequestNotificationPermission()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
                AnimatedVisibility(visible = notificationEnabled) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notify me",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        listOf(1, 3, 7).forEach { days ->
                            FilterChip(
                                selected = notificationDaysBefore == days,
                                onClick = { onNotificationDaysBeforeChange(days) },
                                label = {
                                    Text(
                                        "$days day${if (days > 1) "s" else ""}",
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSubmit,
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
                    text = "Add Subscription",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
@Composable
private fun SubscriptionBillingCycleToggle(
    selectedCycle: BillingCycle,
    onCycleSelected: (BillingCycle) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Billing Cycle",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = BillingCycle.entries.size,
                key = { BillingCycle.entries[it].name }
            ) { index ->
                val cycle = BillingCycle.entries[index]
                SubscriptionBillingCycleChip(
                    cycle = cycle,
                    isSelected = selectedCycle == cycle,
                    onSelected = { onCycleSelected(cycle) }
                )
            }
        }
    }
}
@Composable
private fun SubscriptionBillingCycleChip(
    cycle: BillingCycle,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onSelected,
        label = { 
            Text(
                cycle.name.lowercase().replaceFirstChar { it.uppercase() }
            ) 
        },
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
@Composable
private fun SubscriptionDateField(
    selectedDate: Date,
    onDateClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    OutlinedTextField(
        value = dateFormatter.format(selectedDate),
        onValueChange = { },
        label = { Text("Starting Date") },
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
        },
        shape = RoundedCornerShape(12.dp)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionDatePickerDialog(
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