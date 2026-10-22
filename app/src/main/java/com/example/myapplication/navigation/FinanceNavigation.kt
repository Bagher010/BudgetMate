package com.example.myapplication.navigation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myapplication.ui.motion.MaterialMotion
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.AddContactScreen
import com.example.myapplication.ui.screens.AddTransactionScreen
import com.example.myapplication.ui.screens.AddSubscriptionScreen
import com.example.myapplication.ui.screens.ContactDetailScreen
import com.example.myapplication.ui.screens.ContactsScreen
import com.example.myapplication.ui.screens.EditTransactionScreen
import com.example.myapplication.ui.screens.EditSubscriptionScreen
import com.example.myapplication.ui.screens.HomeScreen
import com.example.myapplication.ui.screens.StatisticsScreen
import com.example.myapplication.ui.screens.SubscriptionScreen
import com.example.myapplication.ui.screens.TransactionsScreen
import com.example.myapplication.ui.screens.SettingsScreen
import com.example.myapplication.ui.screens.AboutScreen
import com.example.myapplication.ui.screens.NotificationsScreen
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import com.example.myapplication.ui.theme.ColorModeState
@Stable
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object AddTransaction : Screen("add_transaction")
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }
    object Statistics : Screen("statistics")
    object Subscriptions : Screen("subscriptions")
    object AddSubscription : Screen("add_subscription")
    object EditSubscription : Screen("edit_subscription/{subscriptionId}") {
        fun createRoute(subscriptionId: Long) = "edit_subscription/$subscriptionId"
    }
    object Contacts : Screen("contacts")
    object AddContact : Screen("add_contact")
    object ContactDetail : Screen("contact_detail/{contactId}") {
        fun createRoute(contactId: Long) = "contact_detail/$contactId"
    }
    object Settings : Screen("settings")
    object About : Screen("about")
    object Notifications : Screen("notifications")
}
@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun FinanceNavigation(
    navController: NavHostController,
    sharedViewModel: FinanceViewModel,
    colorModeState: ColorModeState,
    modifier: Modifier = Modifier,
    onScrollDirectionChange: (Boolean) -> Unit = {}
) {
    val enterTransitions = remember {
        mapOf<String, Map<String, androidx.compose.animation.EnterTransition>>(
            Screen.Home.route to mapOf(
                Screen.Transactions.route to MaterialMotion.sharedAxisXEnter(forward = true),
                Screen.Statistics.route to MaterialMotion.sharedAxisXEnter(forward = true),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXEnter(forward = true),
                Screen.AddTransaction.route to MaterialMotion.sharedAxisYEnter(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYEnter(forward = true)
            ),
            Screen.Transactions.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisXEnter(forward = true),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXEnter(forward = true),
                Screen.AddTransaction.route to MaterialMotion.sharedAxisYEnter(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYEnter(forward = true)
            ),
            Screen.Statistics.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXEnter(forward = true),
                Screen.AddTransaction.route to MaterialMotion.sharedAxisYEnter(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYEnter(forward = true)
            ),
            Screen.Subscriptions.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Contacts.route to MaterialMotion.sharedAxisXEnter(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYEnter(forward = true)
            ),
            Screen.Contacts.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXEnter(forward = false),
                Screen.AddContact.route to MaterialMotion.sharedAxisYEnter(forward = true)
            ),
            Screen.AddTransaction.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisYEnter(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisYEnter(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisYEnter(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisYEnter(forward = false)
            ),
            Screen.AddSubscription.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisYEnter(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisYEnter(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisYEnter(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisYEnter(forward = false)
            ),
            Screen.AddContact.route to mapOf(
                Screen.Contacts.route to MaterialMotion.sharedAxisYEnter(forward = false)
            )
        )
    }
    val exitTransitions = remember {
        mapOf<String, Map<String, androidx.compose.animation.ExitTransition>>(
            Screen.Home.route to mapOf(
                Screen.Transactions.route to MaterialMotion.sharedAxisXExit(forward = true),
                Screen.Statistics.route to MaterialMotion.sharedAxisXExit(forward = true),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXExit(forward = true),
                Screen.AddTransaction.route to MaterialMotion.sharedAxisYExit(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYExit(forward = true)
            ),
            Screen.Transactions.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisXExit(forward = true),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXExit(forward = true),
                Screen.AddTransaction.route to MaterialMotion.sharedAxisYExit(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYExit(forward = true)
            ),
            Screen.Statistics.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXExit(forward = true),
                Screen.AddTransaction.route to MaterialMotion.sharedAxisYExit(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYExit(forward = true)
            ),
            Screen.Subscriptions.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Contacts.route to MaterialMotion.sharedAxisXExit(forward = true),
                Screen.AddSubscription.route to MaterialMotion.sharedAxisYExit(forward = true)
            ),
            Screen.Contacts.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisXExit(forward = false),
                Screen.AddContact.route to MaterialMotion.sharedAxisYExit(forward = true)
            ),
            Screen.AddTransaction.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisYExit(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisYExit(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisYExit(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisYExit(forward = false)
            ),
            Screen.AddSubscription.route to mapOf(
                Screen.Home.route to MaterialMotion.sharedAxisYExit(forward = false),
                Screen.Transactions.route to MaterialMotion.sharedAxisYExit(forward = false),
                Screen.Statistics.route to MaterialMotion.sharedAxisYExit(forward = false),
                Screen.Subscriptions.route to MaterialMotion.sharedAxisYExit(forward = false)
            ),
            Screen.AddContact.route to mapOf(
                Screen.Contacts.route to MaterialMotion.sharedAxisYExit(forward = false)
            )
        )
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) 
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            }
        ) {
            HomeScreen(
                viewModel = sharedViewModel,
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transactions.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                            inclusive = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToContacts = {
                    navController.navigate(Screen.Contacts.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                },
                onScrollDirectionChange = onScrollDirectionChange
            )
        }
        composable(
            route = Screen.Transactions.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            }
        ) {
            TransactionsScreen(
                viewModel = sharedViewModel,
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.route)
                },
                onScrollDirectionChange = onScrollDirectionChange,
                onEditTransaction = { transactionId ->
                    navController.navigate(Screen.EditTransaction.createRoute(transactionId))
                }
            )
        }
        composable(
            route = Screen.AddTransaction.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.sharedAxisYEnter(forward = true) 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.sharedAxisYExit(forward = false) 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.sharedAxisYEnter(forward = true) 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.sharedAxisYExit(forward = false) 
            }
        ) {
            AddTransactionScreen(
                viewModel = sharedViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Statistics.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            }
        ) {
            StatisticsScreen(
                viewModel = sharedViewModel
            )
        }
        composable(
            route = Screen.Subscriptions.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            }
        ) {
            SubscriptionScreen(
                viewModel = sharedViewModel,
                onNavigateToAddSubscription = {
                    navController.navigate(Screen.AddSubscription.route)
                },
                onScrollDirectionChange = onScrollDirectionChange,
                onEditSubscription = { subscriptionId ->
                    navController.navigate(Screen.EditSubscription.createRoute(subscriptionId))
                }
            )
        }
        composable(
            route = Screen.AddSubscription.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.sharedAxisYEnter(forward = true) 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.sharedAxisYExit(forward = false) 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.sharedAxisYEnter(forward = true) 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.sharedAxisYExit(forward = false) 
            }
        ) {
            AddSubscriptionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubscriptionAdded = { name, amount, description, billingCycle, startDate, category, color, iconName ->
                    sharedViewModel.addSubscription(
                        name = name,
                        amount = amount,
                        description = description,
                        billingCycle = billingCycle,
                        startDate = startDate,
                        category = category,
                        color = color,
                        iconName = iconName
                    )
                }
            )
        }
        composable(
            route = Screen.EditTransaction.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType }
            ),
            enterTransition = { MaterialMotion.sharedAxisYEnter(forward = true) },
            exitTransition = { MaterialMotion.sharedAxisYExit(forward = false) },
            popEnterTransition = { MaterialMotion.sharedAxisYEnter(forward = true) },
            popExitTransition = { MaterialMotion.sharedAxisYExit(forward = false) }
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: return@composable
            val transactions by sharedViewModel.transactions.collectAsState()
            val transaction = transactions.find { it.transaction.id == transactionId }?.transaction
            if (transaction != null) {
                EditTransactionScreen(
                    transaction = transaction,
                    viewModel = sharedViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            route = Screen.EditSubscription.route,
            arguments = listOf(
                navArgument("subscriptionId") { type = NavType.LongType }
            ),
            enterTransition = { MaterialMotion.sharedAxisYEnter(forward = true) },
            exitTransition = { MaterialMotion.sharedAxisYExit(forward = false) },
            popEnterTransition = { MaterialMotion.sharedAxisYEnter(forward = true) },
            popExitTransition = { MaterialMotion.sharedAxisYExit(forward = false) }
        ) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments?.getLong("subscriptionId") ?: return@composable
            val subscriptions by sharedViewModel.subscriptions.collectAsState()
            val subscription = subscriptions.find { it.id == subscriptionId }
            if (subscription != null) {
                EditSubscriptionScreen(
                    subscription = subscription,
                    viewModel = sharedViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            route = Screen.Contacts.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.fadeEnter 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.fadeExit 
            }
        ) {
            ContactsScreen(
                viewModel = sharedViewModel,
                onNavigateToAddContact = {
                    navController.navigate(Screen.AddContact.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onScrollDirectionChange = onScrollDirectionChange,
                onContactClick = { contactId ->
                    navController.navigate(Screen.ContactDetail.createRoute(contactId))
                },
                onEditContact = { contactId ->
                    navController.navigate(Screen.ContactDetail.createRoute(contactId))
                }
            )
        }
        composable(
            route = Screen.AddContact.route,
            enterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.sharedAxisYEnter(forward = true) 
            },
            exitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.sharedAxisYExit(forward = false) 
            },
            popEnterTransition = { 
                enterTransitions[targetState.destination.route]?.get(initialState.destination.route) 
                    ?: MaterialMotion.sharedAxisYEnter(forward = true) 
            },
            popExitTransition = { 
                exitTransitions[initialState.destination.route]?.get(targetState.destination.route) 
                    ?: MaterialMotion.sharedAxisYExit(forward = false) 
            }
        ) {
            AddContactScreen(
                viewModel = sharedViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.ContactDetail.route,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType }
            ),
            enterTransition = { MaterialMotion.sharedAxisYEnter(forward = true) },
            exitTransition = { MaterialMotion.sharedAxisYExit(forward = false) },
            popEnterTransition = { MaterialMotion.sharedAxisYEnter(forward = true) },
            popExitTransition = { MaterialMotion.sharedAxisYExit(forward = false) }
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getLong("contactId") ?: return@composable
            val contacts by sharedViewModel.contacts.collectAsState()
            val contact = contacts.find { it.id == contactId }
            if (contact != null) {
                ContactDetailScreen(
                    contact = contact,
                    viewModel = sharedViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(
            route = Screen.Settings.route,
            enterTransition = { MaterialMotion.fadeThroughEnter },
            exitTransition = { MaterialMotion.fadeThroughExit },
            popEnterTransition = { MaterialMotion.fadeThroughEnter },
            popExitTransition = { MaterialMotion.fadeThroughExit }
        ) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                colorModeState = colorModeState
            )
        }
        composable(
            route = Screen.About.route,
            enterTransition = { MaterialMotion.fadeThroughEnter },
            exitTransition = { MaterialMotion.fadeThroughExit },
            popEnterTransition = { MaterialMotion.fadeThroughEnter },
            popExitTransition = { MaterialMotion.fadeThroughExit }
        ) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Notifications.route,
            enterTransition = { MaterialMotion.fadeThroughEnter },
            exitTransition = { MaterialMotion.fadeThroughExit },
            popEnterTransition = { MaterialMotion.fadeThroughEnter },
            popExitTransition = { MaterialMotion.fadeThroughExit }
        ) {
            NotificationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
