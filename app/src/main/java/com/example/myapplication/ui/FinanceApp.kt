package com.example.myapplication.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Stable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import android.app.Activity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.FinanceNavigation
import com.example.myapplication.navigation.Screen
import com.example.myapplication.ui.components.AnimatedExtendedFloatingActionButton
import com.example.myapplication.ui.screens.IntroductionScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.ColorModeState
import com.example.myapplication.ui.theme.AppThemeMode
import com.example.myapplication.ui.theme.rememberColorModeState
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import com.example.myapplication.ui.viewmodel.FinanceViewModelFactory
@Stable
data class BottomNavItem(
    val screen: Screen,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector,
    val label: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp() {
    val colorModeState = rememberColorModeState()
    val context = LocalContext.current
    val onboardingPreferences = remember(context) {
        context.getSharedPreferences("app_preferences", android.content.Context.MODE_PRIVATE)
    }
    var hasCompletedIntroduction by remember {
        mutableStateOf(onboardingPreferences.getBoolean("introduction_completed", false))
    }
    val application = context.applicationContext as com.example.myapplication.FinanceApplication
    val repository = application.repository
    val sharedViewModel: FinanceViewModel = viewModel(
        factory = FinanceViewModelFactory(repository)
    )
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (colorModeState.themeMode) {
        AppThemeMode.SYSTEM -> systemDarkTheme
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
    MyApplicationTheme(darkTheme = darkTheme) {
        if (hasCompletedIntroduction) {
            FinanceAppContent(
                sharedViewModel = sharedViewModel,
                colorModeState = colorModeState
            )
        } else {
            IntroductionScreen(
                onGetStarted = {
                    onboardingPreferences.edit()
                        .putBoolean("introduction_completed", true)
                        .apply()
                    hasCompletedIntroduction = true
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAppContent(
    sharedViewModel: FinanceViewModel,
    colorModeState: ColorModeState
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination by remember(navBackStackEntry?.destination) {
        derivedStateOf { navBackStackEntry?.destination }
    }
    val hapticFeedback = LocalHapticFeedback.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        (context as? Activity)?.reportFullyDrawn()
    }
    val bottomNavItems = remember {
        listOf(
            BottomNavItem(
                screen = Screen.Home, 
                iconFilled = Icons.Filled.Home, 
                iconOutlined = Icons.Outlined.Home, 
                label = "Home"
            ),
            BottomNavItem(
                screen = Screen.Transactions, 
                iconFilled = Icons.AutoMirrored.Filled.List, 
                iconOutlined = Icons.AutoMirrored.Outlined.List, 
                label = "Transactions"
            ),
            BottomNavItem(
                screen = Screen.Subscriptions, 
                iconFilled = Icons.Filled.AccountBalanceWallet, 
                iconOutlined = Icons.Outlined.AccountBalanceWallet, 
                label = "Subscriptions"
            ),
            BottomNavItem(
                screen = Screen.Statistics, 
                iconFilled = Icons.Filled.Info, 
                iconOutlined = Icons.Outlined.Info, 
                label = "Statistics"
            )
        )
    }
    val bottomNavRoutes = remember { bottomNavItems.map { it.screen.route } }
    var isNavigatingToAddScreen by remember { mutableStateOf(false) }
    val showBottomBar by remember {
        derivedStateOf {
            !isNavigatingToAddScreen && (currentDestination?.route in bottomNavRoutes)
        }
    }
    val showFab = remember(currentDestination?.route) { 
        val route = currentDestination?.route
        route != Screen.AddTransaction.route && 
        route != Screen.AddSubscription.route &&
        route != Screen.AddContact.route &&
        route != Screen.Statistics.route &&
        route != Screen.Settings.route &&
        route != Screen.About.route &&
        route != Screen.Notifications.route &&
        route?.startsWith("edit_transaction") != true &&
        route?.startsWith("edit_subscription") != true &&
        route?.startsWith("contact_detail") != true
    }
    val isSubscriptionScreen = remember(currentDestination?.route) {
        currentDestination?.route == Screen.Subscriptions.route
    }
    val isContactsScreen = remember(currentDestination?.route) {
        currentDestination?.route == Screen.Contacts.route
    }
    var expandedFab by remember { mutableStateOf(true) }
    val onScrollDirectionChange = remember {
        { shouldExpand: Boolean ->
            expandedFab = shouldExpand
        }
    }
    val onFabClick = remember(hapticFeedback, navController, isSubscriptionScreen, isContactsScreen) {
        {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            isNavigatingToAddScreen = true
            when {
                isSubscriptionScreen -> navController.navigate(Screen.AddSubscription.route)
                isContactsScreen -> navController.navigate(Screen.AddContact.route)
                else -> navController.navigate(Screen.AddTransaction.route)
            }
        }
    }
    LaunchedEffect(currentDestination?.route) {
        if (currentDestination?.route == Screen.AddSubscription.route || 
            currentDestination?.route == Screen.AddTransaction.route ||
            currentDestination?.route == Screen.AddContact.route) {
            isNavigatingToAddScreen = false
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0),
            topBar = {
            },
            bottomBar = {
                if (showBottomBar) {
                    ColoredNavigationBar(
                        items = bottomNavItems,
                        currentDestination = currentDestination,
                        viewModel = sharedViewModel,
                        onNavigate = { route ->
                            if (currentDestination?.route != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                        inclusive = false 
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (showFab && !isContactsScreen) {
                    val fabText = when {
                        isSubscriptionScreen -> "Add Subscription"
                        else -> "Add Transaction"
                    }
                    AnimatedExtendedFloatingActionButton(
                        onClick = onFabClick, 
                        expanded = expandedFab, 
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = fabText
                            )
                        },
                        text = {
                            Text(
                                text = fabText,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1
                            )
                        }
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            FinanceNavigation(
                navController = navController,
                sharedViewModel = sharedViewModel,
                colorModeState = colorModeState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                onScrollDirectionChange = onScrollDirectionChange
            )
        }
    }
}
@Composable
fun ColoredNavigationBar(
    items: List<BottomNavItem>,
    currentDestination: androidx.navigation.NavDestination?,
    viewModel: FinanceViewModel,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
            val onClick = remember(item.screen.route, viewModel, onNavigate) {
                {
                    if (item.screen.route == Screen.Statistics.route) {
                        viewModel.ensureStatisticsReady()
                    }
                    onNavigate(item.screen.route)
                }
            }
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = if (isSelected) item.iconFilled else item.iconOutlined, 
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    ) 
                },
                label = { 
                    Text(
                        text = item.label,
                        fontSize = 12.sp
                    ) 
                },
                selected = isSelected,
                onClick = onClick, 
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    }
}
