package com.example.myapplication.ui.viewmodel
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.Transaction
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.data.model.TransactionWithCategory
import com.example.myapplication.data.model.Subscription
import com.example.myapplication.data.model.BillingCycle
import com.example.myapplication.data.model.Contact
import com.example.myapplication.data.model.ContactTransaction
import com.example.myapplication.data.model.ContactTransactionType
import com.example.myapplication.data.model.ContactWithBalance
import com.example.myapplication.data.repository.FinanceRepository
import com.example.myapplication.ui.state.TransactionUiState
import com.example.myapplication.ui.state.BalanceCardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import java.util.Calendar
import java.util.Date
@Stable
data class FinanceUiState(
    val transactions: ImmutableList<TransactionWithCategory> = persistentListOf(),
    val transactionUiStates: ImmutableList<TransactionUiState> = persistentListOf(),
    val categories: ImmutableList<Category> = persistentListOf(),
    val balanceCardState: BalanceCardUiState = BalanceCardUiState.from(0.0, 0.0, 0.0),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val isLoading: Boolean = false 
)
@Stable
data class BalanceData(
    val balance: Double = 0.0,
    val income: Double = 0.0,
    val expense: Double = 0.0
)
@Stable
data class StatisticsData(
    val expensesByCategory: ImmutableList<Pair<Category, Double>> = persistentListOf(), 
    val incomeByCategory: ImmutableList<Pair<Category, Double>> = persistentListOf(),  
    val periodTotals: ImmutableMap<TimePeriod, PeriodTotals> = persistentMapOf(),
    val lastCalculated: Long = 0L 
)
@Stable
data class PeriodTotals(
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val transactionCount: Int = 0
)
enum class TimePeriod(val displayName: String) {
    THIS_WEEK("7 Days"),
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    THIS_YEAR("This Year"),
    ALL_TIME("All Time")
}
enum class SortOrder {
    DATE_DESC,    
    DATE_ASC,     
    AMOUNT_DESC,  
    AMOUNT_ASC    
}
enum class TransactionTypeFilter {
    ALL,       
    INCOME,    
    EXPENSE    
}
data class PendingDelete(
    val delete: UndoableDelete,
    val id: Long = System.currentTimeMillis()
)
sealed class UndoableDelete {
    data class TransactionDelete(val transaction: Transaction) : UndoableDelete()
    data class SubscriptionDelete(val subscription: Subscription) : UndoableDelete()
    data class ContactDelete(val contact: Contact) : UndoableDelete()
    data class ContactTransactionDelete(val transaction: ContactTransaction) : UndoableDelete()
}
@OptIn(FlowPreview::class)
class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        FinanceUiState(
            transactions = persistentListOf(),
            categories = persistentListOf(),
            balance = 0.0,
            totalIncome = 0.0,
            totalExpense = 0.0,
            isLoading = false
        )
    )
    val transactions: StateFlow<ImmutableList<TransactionWithCategory>> = 
        _uiState.map { it.transactions }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )
    val balance: StateFlow<Double> = 
        _uiState.map { it.balance }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
    val totalIncome: StateFlow<Double> = 
        _uiState.map { it.totalIncome }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
    val totalExpense: StateFlow<Double> = 
        _uiState.map { it.totalExpense }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
    val isLoading: StateFlow<Boolean> = 
        _uiState.map { it.isLoading }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    val categories: StateFlow<ImmutableList<Category>> = 
        _uiState.map { it.categories }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )
    private val _statisticsData = MutableStateFlow(StatisticsData())
    val statisticsData: StateFlow<StatisticsData> = _statisticsData.asStateFlow()
    private val transactionsCache = mutableListOf<TransactionWithCategory>()
    private val categoriesCache = mutableListOf<Category>()
    private var lastStatisticsCalculation = 0L
    private var lastTransactionCount = 0
    private var lastTransactionDataHash = 0L 
    private val statisticsCacheDuration = 5000L 
    private val periodRanges = mutableMapOf<TimePeriod, Pair<Date, Date>>()
    private val searchDebouncer = MutableStateFlow("")
    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.DATE_DESC)
    private val _typeFilter = MutableStateFlow(TransactionTypeFilter.ALL)
    private val _categoryFilter = MutableStateFlow<Long?>(null)
    private val _pendingDelete = MutableStateFlow<PendingDelete?>(null)
    val pendingDelete: StateFlow<PendingDelete?> = _pendingDelete.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    val typeFilter: StateFlow<TransactionTypeFilter> = _typeFilter.asStateFlow()
    val categoryFilter: StateFlow<Long?> = _categoryFilter.asStateFlow()
    val filteredTransactions: StateFlow<ImmutableList<TransactionWithCategory>> =
        combine(
            transactions, 
            _searchQuery, 
            _sortOrder, 
            _typeFilter,
            _categoryFilter
        ) { transactions, query, order, typeFilter, categoryFilter ->
            var filtered = if (query.isBlank()) {
                transactions
            } else {
                val normalizedQuery = query.trim().replace("\\s+".toRegex(), " ")
                transactions.filter { transaction ->
                    val normalizedDescription = transaction.transaction.description.trim().replace("\\s+".toRegex(), " ")
                    val normalizedCategory = transaction.category.name.trim().replace("\\s+".toRegex(), " ")
                    normalizedDescription.contains(normalizedQuery, ignoreCase = true) ||
                    normalizedCategory.contains(normalizedQuery, ignoreCase = true)
                }
            }
            filtered = when (typeFilter) {
                TransactionTypeFilter.ALL -> filtered
                TransactionTypeFilter.INCOME -> filtered.filter { it.transaction.type == TransactionType.INCOME }
                TransactionTypeFilter.EXPENSE -> filtered.filter { it.transaction.type == TransactionType.EXPENSE }
            }
            filtered = if (categoryFilter != null) {
                filtered.filter { it.category.id == categoryFilter }
            } else {
                filtered
            }
            val sorted = when (order) {
                SortOrder.DATE_DESC -> filtered.sortedByDescending { it.transaction.date }
                SortOrder.DATE_ASC -> filtered.sortedBy { it.transaction.date }
                SortOrder.AMOUNT_DESC -> filtered.sortedByDescending { it.transaction.amount }
                SortOrder.AMOUNT_ASC -> filtered.sortedBy { it.transaction.amount }
            }
            sorted.toImmutableList()
        }
        .distinctUntilChanged() 
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = persistentListOf()
        )
    private val combinedDataFlow = combine(
        repository.getAllTransactionsWithCategory(),
        repository.getBalanceFlow()
    ) { transactions, balanceData ->
        Pair(transactions, balanceData)
    }.conflate() 
    init {
        updatePeriodRanges()
        startCombinedDataFlow()
        viewModelScope.launch {
            loadAllDataOnce()
        }
        viewModelScope.launch {
            calculateAllStatistics()
        }
        searchDebouncer
            .debounce(300) 
            .distinctUntilChanged()
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }
    private suspend fun loadAllDataOnce() {
        try {
            repository.getAllCategories().collect { categories ->
                categoriesCache.clear()
                categoriesCache.addAll(categories)
                _uiState.value = _uiState.value.copy(categories = categories.toImmutableList())
            }
        } catch (e: Exception) {
            Log.e("FinanceViewModel", "Error loading initial data", e)
        }
    }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }
    fun updateTypeFilter(filter: TransactionTypeFilter) {
        _typeFilter.value = filter
    }
    fun updateCategoryFilter(categoryId: Long?) {
        _categoryFilter.value = categoryId
    }
    private suspend fun performSearch(query: String) {
        if (query.isBlank()) {
        } else {
            val searchResults = repository.searchTransactions(query)
            _uiState.value = _uiState.value.copy(transactions = searchResults.toImmutableList())
        }
    }
    private fun startCombinedDataFlow() {
        viewModelScope.launch {
            try {
                combinedDataFlow.collect { (transactions, balanceData) ->
                    transactionsCache.clear()
                    transactionsCache.addAll(transactions)
                    _uiState.value = _uiState.value.copy(
                        transactions = transactions.toImmutableList(),
                        balance = balanceData.balance,
                        totalIncome = balanceData.income,
                        totalExpense = balanceData.expense
                    )
                    calculateAllStatistics()
                }
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error in combined data flow", e)
            }
        }
    }
    private fun updatePeriodRanges() {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfToday = calendar.time
        calendar.time = now
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.time
        periodRanges[TimePeriod.THIS_WEEK] = Pair(weekStart, endOfToday)
        calendar.time = now
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.time
        periodRanges[TimePeriod.THIS_MONTH] = Pair(monthStart, endOfToday)
        calendar.time = now
        calendar.add(Calendar.MONTH, -5)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val sixMonthsStart = calendar.time
        periodRanges[TimePeriod.LAST_MONTH] = Pair(sixMonthsStart, endOfToday)
        calendar.time = now
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val yearStart = calendar.time
        periodRanges[TimePeriod.THIS_YEAR] = Pair(yearStart, endOfToday)
        periodRanges[TimePeriod.ALL_TIME] = Pair(Date(0), endOfToday)
    }
    fun ensureStatisticsReady() {
        viewModelScope.launch {
            if (_statisticsData.value.periodTotals.isEmpty()) {
                calculateAllStatistics()
            }
        }
    }
    private suspend fun calculateAllStatistics() = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        val currentTransactionCount = transactionsCache.size
        val currentDataHash = transactionsCache.hashCode().toLong()
        if (currentTransactionCount == lastTransactionCount &&
            currentDataHash == lastTransactionDataHash) {
            return@withContext
        }
        updatePeriodRanges()
        lastStatisticsCalculation = now
        lastTransactionCount = currentTransactionCount
        lastTransactionDataHash = currentDataHash
        try {
            val transactions = transactionsCache.toList() 
            val periodCalculations = TimePeriod.entries.map { period ->
                async {
                    val filtered = filterTransactionsByPeriodOptimized(transactions, period)
                    period to PeriodTotals(
                        income = filtered.asSequence()
                            .filter { it.transaction.type == TransactionType.INCOME }
                            .sumOf { it.transaction.amount },
                        expense = filtered.asSequence()
                            .filter { it.transaction.type == TransactionType.EXPENSE }
                            .sumOf { it.transaction.amount },
                        transactionCount = filtered.size
                    )
                }
            }
            val periodTotals = periodCalculations.awaitAll().toMap()
            val expensesByCategory = transactions
                .filter { it.transaction.type == TransactionType.EXPENSE }
                .groupBy { it.category }
                .mapValues { it.value.sumOf { transaction -> transaction.transaction.amount } }
                .toList()
                .sortedByDescending { it.second } 
            val incomeByCategory = transactions
                .filter { it.transaction.type == TransactionType.INCOME }
                .groupBy { it.category }
                .mapValues { it.value.sumOf { transaction -> transaction.transaction.amount } }
                .toList()
                .sortedByDescending { it.second } 
            _statisticsData.value = StatisticsData(
                expensesByCategory = expensesByCategory.toImmutableList(),
                incomeByCategory = incomeByCategory.toImmutableList(),
                periodTotals = periodTotals.toImmutableMap(),
                lastCalculated = now
            )
        } catch (e: Exception) {
            Log.e("FinanceViewModel", "Error calculating statistics", e)
        }
    }
    private fun filterTransactionsByPeriodOptimized(
        transactions: List<TransactionWithCategory>,
        period: TimePeriod
    ): List<TransactionWithCategory> {
        val (start, end) = periodRanges[period] ?: return emptyList()
        return when (period) {
            TimePeriod.ALL_TIME -> transactions
            else -> {
                transactions.filter { 
                    it.transaction.date >= start && it.transaction.date <= end
                }
            }
        }
    }
    fun addTransaction(
        amount: Double,
        description: String,
        categoryId: Long,
        type: TransactionType,
        date: Date = Date()
    ) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    amount = amount,
                    description = description,
                    categoryId = categoryId,
                    type = type,
                    date = date
                )
                repository.insertTransaction(transaction)
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error adding transaction", e)
            }
        }
    }
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _pendingDelete.value = PendingDelete(UndoableDelete.TransactionDelete(transaction))
        }
    }
    fun undoDelete() {
        viewModelScope.launch {
            when (val pending = _pendingDelete.value?.delete) {
                is UndoableDelete.TransactionDelete -> {
                    repository.insertTransaction(pending.transaction)
                }
                is UndoableDelete.SubscriptionDelete -> {
                    repository.insertSubscription(pending.subscription)
                }
                is UndoableDelete.ContactDelete -> {
                    repository.insertContact(pending.contact)
                }
                is UndoableDelete.ContactTransactionDelete -> {
                    repository.insertContactTransaction(pending.transaction)
                }
                null -> {}
            }
            _pendingDelete.value = null
        }
    }
    fun clearPendingDelete() {
        _pendingDelete.value = null
    }
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error updating transaction", e)
            }
        }
    }
    val subscriptions: StateFlow<ImmutableList<Subscription>> = 
        repository.getAllSubscriptions()
            .map { it.toImmutableList() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = persistentListOf()
            )
    val activeSubscriptions: StateFlow<ImmutableList<Subscription>> = 
        repository.getActiveSubscriptions()
            .map { it.toImmutableList() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = persistentListOf()
            )
    val monthlySubscriptionTotal: StateFlow<Double> =
        repository.getMonthlyEquivalentTotal()
            .map { it ?: 0.0 }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )
    fun addSubscription(
        name: String,
        amount: Double,
        description: String,
        billingCycle: BillingCycle,
        startDate: Date,
        category: String = "Subscription",
        color: String = "#6200EE",
        iconName: String = "subscription"
    ) {
        viewModelScope.launch {
            try {
                val nextPaymentDate = calculateNextPaymentDate(startDate, billingCycle)
                val subscription = Subscription(
                    name = name,
                    amount = amount,
                    billingCycle = billingCycle,
                    category = category,
                    startDate = startDate,
                    nextPaymentDate = nextPaymentDate,
                    isActive = true,
                    reminderEnabled = true,
                    reminderDaysBefore = 3,
                    color = color,
                    description = description,
                    iconName = iconName
                )
                repository.insertSubscription(subscription)
                Log.d("FinanceViewModel", "Subscription added: $name")
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error adding subscription", e)
            }
        }
    }
    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch {
            try {
                repository.updateSubscription(subscription)
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error updating subscription", e)
            }
        }
    }
    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            try {
                repository.deleteSubscription(subscription)
                _pendingDelete.value = PendingDelete(UndoableDelete.SubscriptionDelete(subscription))
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error deleting subscription", e)
            }
        }
    }
    fun toggleSubscriptionStatus(subscription: Subscription) {
        viewModelScope.launch {
            try {
                repository.updateSubscriptionStatus(subscription.id, !subscription.isActive)
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error toggling subscription status", e)
            }
        }
    }
    private fun calculateNextPaymentDate(startDate: Date, billingCycle: BillingCycle): Date {
        val calendar = Calendar.getInstance().apply {
            time = startDate
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
        when (billingCycle) {
            BillingCycle.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            BillingCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            BillingCycle.QUARTERLY -> calendar.add(Calendar.MONTH, 3)
            BillingCycle.YEARLY -> calendar.add(Calendar.YEAR, 1)
        }
        while (calendar.timeInMillis < today.timeInMillis) {
            when (billingCycle) {
                BillingCycle.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                BillingCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
                BillingCycle.QUARTERLY -> calendar.add(Calendar.MONTH, 3)
                BillingCycle.YEARLY -> calendar.add(Calendar.YEAR, 1)
            }
        }
        return calendar.time
    }
    val contacts: StateFlow<ImmutableList<Contact>> = 
        repository.getAllContacts()
            .map { it.toImmutableList() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = persistentListOf()
            )
    val totalOwedToUser: StateFlow<Double> =
        repository.getTotalOwedToUser()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )
    val totalUserOwes: StateFlow<Double> =
        repository.getTotalUserOwes()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )
    fun addContact(
        name: String,
        phoneNumber: String = "",
        email: String = "",
        avatarColor: String = "#6200EE",
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                val contact = Contact(
                    name = name,
                    phoneNumber = phoneNumber,
                    email = email,
                    avatarColor = avatarColor,
                    notes = notes
                )
                repository.insertContact(contact)
                Log.d("FinanceViewModel", "Contact added: $name")
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error adding contact", e)
            }
        }
    }
    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            try {
                repository.updateContact(contact)
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error updating contact", e)
            }
        }
    }
    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            try {
                repository.deleteContact(contact)
                _pendingDelete.value = PendingDelete(UndoableDelete.ContactDelete(contact))
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error deleting contact", e)
            }
        }
    }
    fun addContactTransaction(
        contactId: Long,
        amount: Double,
        description: String,
        type: ContactTransactionType,
        date: Date = Date()
    ) {
        viewModelScope.launch {
            try {
                val transaction = ContactTransaction(
                    contactId = contactId,
                    amount = amount,
                    description = description,
                    type = type,
                    date = date
                )
                repository.insertContactTransaction(transaction)
                Log.d("FinanceViewModel", "Contact transaction added: $description")
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error adding contact transaction", e)
            }
        }
    }
    fun updateContactTransaction(transaction: ContactTransaction) {
        viewModelScope.launch {
            try {
                repository.updateContactTransaction(transaction)
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error updating contact transaction", e)
            }
        }
    }
    fun deleteContactTransaction(transaction: ContactTransaction) {
        viewModelScope.launch {
            try {
                repository.deleteContactTransaction(transaction)
                _pendingDelete.value = PendingDelete(UndoableDelete.ContactTransactionDelete(transaction))
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error deleting contact transaction", e)
            }
        }
    }
    fun settleContactTransactions(contactId: Long) {
        viewModelScope.launch {
            try {
                repository.settleAllTransactions(contactId, System.currentTimeMillis())
                Log.d("FinanceViewModel", "Contact transactions settled for contact: $contactId")
            } catch (e: Exception) {
                Log.e("FinanceViewModel", "Error settling contact transactions", e)
            }
        }
    }
    fun getContactTransactions(contactId: Long): StateFlow<ImmutableList<ContactTransaction>> =
        repository.getTransactionsByContact(contactId)
            .map { it.toImmutableList() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = persistentListOf()
            )
    fun getContactBalance(contactId: Long): StateFlow<Double> =
        repository.getContactBalance(contactId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )
}
