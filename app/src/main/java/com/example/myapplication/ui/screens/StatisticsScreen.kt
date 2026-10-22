package com.example.myapplication.ui.screens
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kotlin.math.roundToInt
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.Category
import com.example.myapplication.data.model.TransactionWithCategory
import com.example.myapplication.data.model.TransactionType
import com.example.myapplication.ui.viewmodel.FinanceViewModel
import com.example.myapplication.ui.viewmodel.TimePeriod
import com.example.myapplication.utils.getCategoryIcon
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.pow
private fun calculateNiceUpperBound(maxValue: Double): Double {
    if (maxValue <= 0) return 1.0
    val increment = when {
        maxValue <= 10 -> 1.0
        maxValue <= 50 -> 5.0
        maxValue <= 100 -> 10.0
        maxValue <= 500 -> 20.0
        maxValue <= 1000 -> 50.0
        maxValue <= 5000 -> 100.0
        maxValue <= 10000 -> 500.0
        else -> 1000.0
    }
    val rounded = ceil(maxValue / increment) * increment
    return if (rounded <= maxValue) rounded + increment else rounded
}
enum class ChartType {
    BAR, LINE, DONUT
}
@Composable
fun StatisticsScreen(
    viewModel: FinanceViewModel
) {
    val statisticsData by viewModel.statisticsData.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val currencyFormatter = remember { 
        NumberFormat.getCurrencyInstance().apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }
    var selectedPeriod by remember { mutableStateOf(TimePeriod.THIS_MONTH) }
    var selectedChartType by remember { mutableStateOf(ChartType.BAR) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var trackerValue by remember { mutableStateOf<Double?>(null) }
    var trackerLabel by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(selectedPeriod) {
        selectedCategory = null
    }
    LaunchedEffect(selectedChartType, selectedPeriod, transactions.size, selectedCategory) {
        android.util.Log.d("ChartDebug", "StatisticsScreen: chartType=$selectedChartType, period=$selectedPeriod, transactions=${transactions.size}, category=${selectedCategory?.name}")
    }
    val periodData = statisticsData.periodTotals[selectedPeriod] ?: com.example.myapplication.ui.viewmodel.PeriodTotals()
    val filteredTotal by produceState(
        initialValue = periodData.expense,
        transactions,
        selectedPeriod,
        selectedCategory,
        periodData
    ) { 
        value = withContext(Dispatchers.Default) {
            if (selectedCategory == null) {
                periodData.expense
            } else {
                val categoryId = selectedCategory!!.id
                val expenses = transactions
                    .filter { it.transaction.type == TransactionType.EXPENSE }
                    .filter { it.category.id == categoryId }
                val now = Date()
                val filteredByPeriod = when (selectedPeriod) {
                    TimePeriod.THIS_WEEK -> {
                        val calendar = Calendar.getInstance().apply {
                            time = now
                            add(Calendar.DAY_OF_YEAR, -6)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val weekStart = calendar.time
                        expenses.filter { it.transaction.date in weekStart..now }
                    }
                    TimePeriod.THIS_MONTH -> {
                        val calendar = Calendar.getInstance().apply {
                            time = now
                            set(Calendar.DAY_OF_MONTH, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val monthStart = calendar.time
                        expenses.filter { it.transaction.date in monthStart..now }
                    }
                    TimePeriod.LAST_MONTH -> {
                        val calendar = Calendar.getInstance().apply {
                            time = now
                            add(Calendar.MONTH, -5)
                            set(Calendar.DAY_OF_MONTH, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val sixMonthsAgo = calendar.time
                        expenses.filter { it.transaction.date in sixMonthsAgo..now }
                    }
                    TimePeriod.THIS_YEAR -> {
                        val calendar = Calendar.getInstance().apply {
                            time = now
                            set(Calendar.DAY_OF_YEAR, 1)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val yearStart = calendar.time
                        expenses.filter { it.transaction.date in yearStart..now }
                    }
                    TimePeriod.ALL_TIME -> expenses
                }
                filteredByPeriod.sumOf { it.transaction.amount }
            }
        }
    }
    val expensesByCategory by produceState(
        initialValue = emptyList<Pair<Category, Double>>(),
        transactions,
        selectedPeriod
    ) {
        value = withContext(Dispatchers.Default) {
            calculateExpensesByCategory(transactions, selectedPeriod)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                InsightsHeader(
                    selectedChartType = selectedChartType,
                    onChartTypeSelected = { 
                        android.util.Log.d("ChartDebug", "Chart type selected: $it")
                        selectedChartType = it 
                    }
                )
            }
            item(key = "spent_header_${statisticsData.lastCalculated}") {
                Surface(
                    onClick = { 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = if (trackerValue != null && trackerLabel != null) 
                                getFullTrackerLabel(trackerLabel!!, selectedPeriod) 
                            else "Spent",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (trackerValue != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currencyFormatter.format(trackerValue ?: filteredTotal),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (trackerValue != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.alpha(if (trackerValue == null) 1f else 0f)
                        ) {
                            Text(
                                text = getPeriodDescription(selectedPeriod),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            selectedCategory?.let { category ->
                                Text(
                                    text = "• ${category.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            item(key = "chart_${statisticsData.lastCalculated}_${selectedChartType}") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 1.dp
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        when (selectedChartType) {
                            ChartType.BAR -> BarChartSection(
                                transactions = transactions,
                                selectedPeriod = selectedPeriod,
                                currencyFormatter = currencyFormatter,
                                selectedCategory = selectedCategory,
                                onTrackerValueChange = { value, label ->
                                    trackerValue = value
                                    trackerLabel = label
                                }
                            )
                            ChartType.LINE -> LineChartSection(
                                transactions = transactions,
                                selectedPeriod = selectedPeriod,
                                currencyFormatter = currencyFormatter,
                                selectedCategory = selectedCategory,
                                onTrackerValueChange = { value, label ->
                                    trackerValue = value
                                    trackerLabel = label
                                }
                            )
                            ChartType.DONUT -> DonutChartSection(
                                total = filteredTotal,
                                categories = expensesByCategory,
                                currencyFormatter = currencyFormatter,
                                selectedPeriod = selectedPeriod,
                                selectedCategory = selectedCategory,
                                onCategorySelected = { category ->
                                    selectedCategory = if (selectedCategory?.id == category.id) null else category
                                }
                            )
                        }
                    }
                }
            }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 1.dp
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        PeriodSelectorRow(
                            selectedPeriod = selectedPeriod,
                            onPeriodSelected = { selectedPeriod = it }
                        )
                    }
                }
            }
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 1.dp
                ) {
                    CategoryBreakdownSection(
                        categories = expensesByCategory,
                        total = periodData.expense,
                        currencyFormatter = currencyFormatter,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { category ->
                            selectedCategory = if (selectedCategory?.id == category.id) null else category
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InsightsHeader(
    selectedChartType: ChartType,
    onChartTypeSelected: (ChartType) -> Unit
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 60.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                    Text(
            text = "Statistics",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 36.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialShapes.Square.toShape())
                    .background(
                        if (selectedChartType == ChartType.LINE) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            Color.Transparent
                    )
                    .clickable { onChartTypeSelected(ChartType.LINE) }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ShowChart, 
                    contentDescription = "Line Chart",
                    modifier = Modifier.size(24.dp),
                    tint = if (selectedChartType == ChartType.LINE) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialShapes.Square.toShape())
                    .background(
                        if (selectedChartType == ChartType.BAR) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            Color.Transparent
                    )
                    .clickable { onChartTypeSelected(ChartType.BAR) }
            ) {
                Icon(
                    Icons.Default.BarChart, 
                    contentDescription = "Bar Chart",
                    modifier = Modifier.size(24.dp),
                    tint = if (selectedChartType == ChartType.BAR) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialShapes.Square.toShape())
                    .background(
                        if (selectedChartType == ChartType.DONUT) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            Color.Transparent
                    )
                    .clickable { onChartTypeSelected(ChartType.DONUT) }
            ) {
                Icon(
                    Icons.Default.PieChart, 
                    contentDescription = "Donut Chart",
                    modifier = Modifier.size(24.dp),
                    tint = if (selectedChartType == ChartType.DONUT) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
private fun calculateBarPositions(
    width: Float,
    totalBars: Int
): Pair<Float, Float> {
    val barWidth = width / (totalBars + (totalBars - 1) * 1.8f)
    val spacing = barWidth * 1.8f
    return Pair(barWidth, spacing)
}
@Composable
fun BarChartSection(
    transactions: ImmutableList<TransactionWithCategory>,
    selectedPeriod: TimePeriod,
    currencyFormatter: NumberFormat,
    selectedCategory: Category? = null,
    onTrackerValueChange: (Double?, String?) -> Unit = { _, _ -> }
) {
    android.util.Log.d("ChartDebug", "BarChartSection called with period: $selectedPeriod, transactions: ${transactions.size}, category: ${selectedCategory?.name}")
    val (labels, data) = remember(transactions, selectedPeriod, selectedCategory) {
        android.util.Log.d("ChartDebug", "BarChartSection remember block executing")
        calculateChartData(transactions, selectedPeriod, selectedCategory)
    }
    android.util.Log.d("ChartDebug", "BarChartSection labels: $labels, data: $data")
    val maxDailyValue = data.maxOrNull() ?: 0.0
    val maxValue = if (maxDailyValue > 0) calculateNiceUpperBound(maxDailyValue) else 1.0
    val periodTotal = data.sum()
    val actualMaxDailyValue = if (selectedPeriod == TimePeriod.THIS_MONTH) {
        val expenses = transactions.filter { it.transaction.type == TransactionType.EXPENSE }
        val calendar = Calendar.getInstance()
        val now = Date()
        calendar.time = now
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        var maxDaySpending = 0.0
        for (day in 1..lastDayOfMonth) {
            calendar.time = now
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val dayStart = calendar.time
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val dayEnd = calendar.time
            val actualDayEnd = if (dayEnd > now) now else dayEnd
            val daySpending = expenses
                .filter { it.transaction.date in dayStart..actualDayEnd }
                .sumOf { it.transaction.amount }
            if (daySpending > maxDaySpending) {
                maxDaySpending = daySpending
            }
        }
        maxDaySpending
    } else {
        maxDailyValue
    }
    val upperBoundValue = when (selectedPeriod) {
        TimePeriod.THIS_WEEK -> periodTotal
        TimePeriod.THIS_MONTH -> periodTotal
        TimePeriod.LAST_MONTH -> periodTotal
        TimePeriod.THIS_YEAR -> periodTotal
        TimePeriod.ALL_TIME -> periodTotal
    }
    android.util.Log.d("ChartDebug", "BarChart upperBound: periodTotal=$periodTotal, maxDaily=$maxDailyValue, actualMaxDaily=$actualMaxDailyValue, chartMax=$maxValue, upperBound=$upperBoundValue, period=$selectedPeriod")
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(transactions, selectedPeriod, selectedCategory) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    var trackerBarIndex by remember { mutableIntStateOf(-1) }
    var isTrackerActive by remember { mutableStateOf(false) }
    var isTrackerReady by remember { mutableStateOf(false) }
    var lastTrackerBarIndex by remember { mutableIntStateOf(-1) }
    val animatedTrackerIndex = remember { Animatable(0f) }
    LaunchedEffect(trackerBarIndex, isTrackerActive) {
        if (isTrackerActive && trackerBarIndex >= 0) {
            if (lastTrackerBarIndex == -1 || !isTrackerReady) {
                animatedTrackerIndex.snapTo(trackerBarIndex.toFloat())
                isTrackerReady = true
            } else {
                animatedTrackerIndex.animateTo(
                    targetValue = trackerBarIndex.toFloat(),
                    animationSpec = tween(durationMillis = 150)
                )
            }
            lastTrackerBarIndex = trackerBarIndex
        } else if (!isTrackerActive) {
            isTrackerReady = false
            lastTrackerBarIndex = -1
        }
    }
    LaunchedEffect(transactions, selectedPeriod, selectedCategory) {
        trackerBarIndex = -1
        isTrackerActive = false
        isTrackerReady = false
        lastTrackerBarIndex = -1
        onTrackerValueChange(null, null)
    }
    LaunchedEffect(isTrackerActive, trackerBarIndex) {
        if (isTrackerActive && trackerBarIndex >= 0 && trackerBarIndex < data.size) {
            onTrackerValueChange(data[trackerBarIndex], labels[trackerBarIndex])
        } else {
            onTrackerValueChange(null, null)
        }
    }
    Column(
                modifier = Modifier
                    .fillMaxWidth()
            .height(280.dp)
    ) {
        var canvasWidthPx by remember { mutableFloatStateOf(0f) }
        var canvasHeightPx by remember { mutableFloatStateOf(0f) }
        Box(
                        modifier = Modifier
                            .fillMaxWidth()
                .weight(1f)
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryColor = MaterialTheme.colorScheme.secondary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val gridLineColor = MaterialTheme.colorScheme.outlineVariant
            val trackerLineColor = MaterialTheme.colorScheme.onSurface
            val trackerValueBgColor = MaterialTheme.colorScheme.surfaceContainerHighest
    Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 4.dp, end = 64.dp, top = 4.dp, bottom = 4.dp)
                    .onGloballyPositioned { coordinates ->
                        canvasWidthPx = coordinates.size.width.toFloat()
                        canvasHeightPx = coordinates.size.height.toFloat()
                    }
                    .pointerInput(labels.size, data) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val longPressTimeout = 300L
                            val longPressResult = withTimeoutOrNull(longPressTimeout) {
                                waitForUpOrCancellation()
                            }
                            if (longPressResult == null) {
                                val totalBars = labels.size
                                if (totalBars > 0 && canvasWidthPx > 0f) {
                                    val (barWidth, spacing) = calculateBarPositions(canvasWidthPx, totalBars)
                                    val touchX = down.position.x
                                    var closestIndex = 0
                                    var minDistance = Float.MAX_VALUE
                                    for (i in 0 until totalBars) {
                                        val barCenterX = i * (barWidth + spacing) + barWidth / 2
                                        val distance = kotlin.math.abs(touchX - barCenterX)
                                        if (distance < minDistance) {
                                            minDistance = distance
                                            closestIndex = i
                                        }
                                    }
                                    trackerBarIndex = closestIndex
                                    isTrackerActive = true
                                    drag(down.id) { change ->
                                        change.consume()
                                        val dragTouchX = change.position.x
                                        var closestIndex = 0
                                        var minDistance = Float.MAX_VALUE
                                        for (i in 0 until totalBars) {
                                            val barCenterX = i * (barWidth + spacing) + barWidth / 2
                                            val distance = kotlin.math.abs(dragTouchX - barCenterX)
                                            if (distance < minDistance) {
                                                minDistance = distance
                                                closestIndex = i
                                            }
                                        }
                                        trackerBarIndex = closestIndex
                                    }
                                    isTrackerActive = false
                                    trackerBarIndex = -1
                                }
                            }
                        }
                    }
            ) {
                val totalBars = labels.size
                val (barWidth, spacing) = calculateBarPositions(size.width, totalBars)
                val chartBottom = size.height
                val chartTop = size.height * 0.2f
                val chartHeight = chartBottom - chartTop
                val gridValues = listOf(
                    maxValue,
                    maxValue * 0.75,
                    maxValue * 0.5,
                    maxValue * 0.25
                )
                gridValues.forEach { value ->
                    val normalizedValue = (value / maxValue).toFloat()
                    val yPosition = chartTop + (1f - normalizedValue) * chartHeight
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, yPosition),
                        end = Offset(size.width, yPosition),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f)
                        )
                    )
                }
                data.forEachIndexed { index, value ->
                    val calculatedHeight = (value / maxValue * chartHeight * animatedProgress.value).toFloat()
                    val barHeight = calculatedHeight.coerceAtMost(chartHeight)
                    val x = index * (barWidth + spacing)
                    val y = chartBottom - barHeight
                    val barColor = when (index % 3) {
                        0 -> primaryColor
                        1 -> secondaryColor
                        else -> tertiaryColor
                    }
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                            barWidth / 2,
                            barWidth / 2
                        )
                    )
                }
                if (isTrackerActive && isTrackerReady && trackerBarIndex >= 0 && trackerBarIndex < data.size) {
                    val animatedIndex = animatedTrackerIndex.value.coerceIn(0f, (data.size - 1).toFloat())
                    val lowerIndex = animatedIndex.toInt().coerceIn(0, data.size - 1)
                    val upperIndex = (lowerIndex + 1).coerceIn(0, data.size - 1)
                    val fraction = animatedIndex - lowerIndex
                    val lowerValue = data[lowerIndex]
                    val upperValue = data[upperIndex]
                    val interpolatedValue = lowerValue + (upperValue - lowerValue) * fraction
                    val trackerBarCenterX = animatedIndex * (barWidth + spacing) + barWidth / 2
                    val normalizedTrackerValue = if (maxValue > 0) (interpolatedValue / maxValue).toFloat() else 0f
                    val trackerValueY = chartTop + (1f - normalizedTrackerValue) * chartHeight
                    val circleRadius = 8.dp.toPx()
                    val lineTopY = 0f
                    drawLine(
                        color = trackerLineColor,
                        start = Offset(trackerBarCenterX, lineTopY),
                        end = Offset(trackerBarCenterX, trackerValueY - circleRadius),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawCircle(
                        color = trackerLineColor,
                        radius = circleRadius,
                        center = Offset(trackerBarCenterX, trackerValueY),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White,
                        radius = circleRadius - 1.dp.toPx(),
                        center = Offset(trackerBarCenterX, trackerValueY)
                    )
                }
            }
            var boxHeightPx by remember { mutableFloatStateOf(0f) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 4.dp, end = 64.dp, top = 4.dp, bottom = 4.dp)
                    .onGloballyPositioned { coordinates ->
                        boxHeightPx = coordinates.size.height.toFloat()
                    }
            ) {
                val density = LocalDensity.current
                val chartTop = boxHeightPx * 0.2f
                val chartHeight = boxHeightPx * 0.8f
                val labelValues = listOf(
                    maxValue,
                    maxValue * 0.75,
                    maxValue * 0.5,
                    maxValue * 0.25
                )
                val gridValues = listOf(
                    maxValue,
                    maxValue * 0.75,
                    maxValue * 0.5,
                    maxValue * 0.25
                )
                labelValues.forEachIndexed { index, labelValue ->
                    val gridValue = gridValues[index]
                    val normalizedValue = (gridValue / maxValue).toFloat()
                    val yPosition = chartTop + (1f - normalizedValue) * chartHeight
                    Text(
                        text = labelValue.roundToInt().toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(
                                x = 68.dp,
                                y = with(density) { yPosition.toDp() } - 6.dp
                            )
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 64.dp)
        ) {
            val totalBars = labels.size
            if (canvasWidthPx > 0f) {
                val (barWidth, spacing) = calculateBarPositions(canvasWidthPx, totalBars)
                labels.forEachIndexed { index, label ->
                    val barStartX = index * (barWidth + spacing)
                    val barCenterX = barStartX + barWidth / 2
                    key(canvasWidthPx, index) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isTrackerActive && index == trackerBarIndex) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isTrackerActive && index == trackerBarIndex) 
                                FontWeight.Bold 
                            else 
                                FontWeight.Normal,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.layout { measurable: Measurable, constraints: androidx.compose.ui.unit.Constraints ->
                                val placeable: Placeable = measurable.measure(constraints)
                                val textWidth = placeable.width
                                val centerX = barCenterX - textWidth / 2f
                                layout(placeable.width, placeable.height) {
                                    placeable.place(
                                        x = centerX.roundToInt(),
                                        y = 0
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
private fun calculateExpensesByCategory(
    transactions: ImmutableList<TransactionWithCategory>,
    period: TimePeriod
): List<Pair<Category, Double>> {
    val now = Date()
    val expenses = transactions.filter { it.transaction.type == TransactionType.EXPENSE }
    val filteredExpenses = when (period) {
        TimePeriod.THIS_WEEK -> {
            val calendar = Calendar.getInstance().apply {
                time = now
                add(Calendar.DAY_OF_YEAR, -6)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val weekStart = calendar.time
            expenses.filter { it.transaction.date in weekStart..now }
        }
        TimePeriod.THIS_MONTH -> {
            val calendar = Calendar.getInstance().apply {
                time = now
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val monthStart = calendar.time
            expenses.filter { it.transaction.date in monthStart..now }
        }
        TimePeriod.LAST_MONTH -> {
            val calendar = Calendar.getInstance().apply {
                time = now
                add(Calendar.MONTH, -5)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val sixMonthsAgo = calendar.time
            expenses.filter { it.transaction.date in sixMonthsAgo..now }
        }
        TimePeriod.THIS_YEAR -> {
            val calendar = Calendar.getInstance().apply {
                time = now
                set(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val yearStart = calendar.time
            expenses.filter { it.transaction.date in yearStart..now }
        }
        TimePeriod.ALL_TIME -> expenses
    }
    return filteredExpenses
        .groupBy { it.category }
        .mapValues { it.value.sumOf { transaction -> transaction.transaction.amount } }
        .toList()
        .sortedByDescending { it.second }
}
private fun calculateChartData(
    transactions: ImmutableList<TransactionWithCategory>,
    period: TimePeriod,
    selectedCategory: Category? = null
): Pair<List<String>, List<Double>> {
    android.util.Log.d("ChartDebug", "=== calculateChartData called ===")
    android.util.Log.d("ChartDebug", "Period: $period, Total transactions: ${transactions.size}, Category: ${selectedCategory?.name}")
    val calendar = Calendar.getInstance()
    val now = calendar.time
    var expenses = transactions.filter { it.transaction.type == TransactionType.EXPENSE }
    if (selectedCategory != null) {
        expenses = expenses.filter { it.category.id == selectedCategory.id }
    }
    android.util.Log.d("ChartDebug", "Expenses count: ${expenses.size}")
    return when (period) {
        TimePeriod.THIS_WEEK -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            calendar.time = now
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val weekStart = calendar.time
            for (i in 0..6) {
                calendar.time = weekStart
                calendar.add(Calendar.DAY_OF_YEAR, i)
                val dayStart = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val dayEnd = calendar.time
                val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(dayStart)
                labels.add(dayName)
                val dayExpense = expenses
                    .filter { it.transaction.date in dayStart..dayEnd }
                    .sumOf { it.transaction.amount }
                data.add(dayExpense)
            }
            Pair(labels, data)
        }
        TimePeriod.THIS_MONTH -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            val currentTime = Date()
            val cal = Calendar.getInstance()
            cal.time = currentTime
            val currentDay = cal.get(Calendar.DAY_OF_MONTH)
            val lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            android.util.Log.d("ChartDebug", "Current date: $currentTime")
            android.util.Log.d("ChartDebug", "Current day: $currentDay, Last day: $lastDayOfMonth")
            android.util.Log.d("ChartDebug", "Total expenses count: ${expenses.size}")
            val ranges = when (lastDayOfMonth) {
                28 -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 28)
                29 -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 29)
                30 -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 30)
                else -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 28, 29 to 31)
            }
            ranges.forEach { (startDay, endDay) ->
                cal.time = currentTime
                cal.set(Calendar.DAY_OF_MONTH, startDay)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val rangeStart = cal.time
                cal.time = currentTime
                cal.set(Calendar.DAY_OF_MONTH, endDay)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val rangeEnd = cal.time
                labels.add("$startDay-$endDay")
                val filtered = expenses.filter { it.transaction.date in rangeStart..rangeEnd }
                android.util.Log.d("ChartDebug", "Range $startDay-$endDay: $rangeStart to $rangeEnd")
                android.util.Log.d("ChartDebug", "Filtered count: ${filtered.size}")
                filtered.forEach {
                    android.util.Log.d("ChartDebug", "  Transaction date: ${it.transaction.date}, amount: ${it.transaction.amount}")
                }
                val rangeExpense = filtered.sumOf { it.transaction.amount }
                android.util.Log.d("ChartDebug", "Range total: $rangeExpense")
                data.add(rangeExpense)
            }
            android.util.Log.d("ChartDebug", "Final data: $data")
            Pair(labels, data)
        }
        TimePeriod.LAST_MONTH -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            for (i in 5 downTo 0) {
                calendar.time = now
                calendar.add(Calendar.MONTH, -i)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val monthStart = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val monthEnd = calendar.time
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(monthStart)
                labels.add(monthName)
                val monthExpense = expenses
                    .filter { it.transaction.date in monthStart..monthEnd }
                    .sumOf { it.transaction.amount }
                data.add(monthExpense)
            }
            Pair(labels, data)
        }
        TimePeriod.THIS_YEAR -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            for (i in 11 downTo 0) {
                calendar.time = now
                calendar.add(Calendar.MONTH, -i)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val monthStart = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val monthEnd = calendar.time
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(monthStart).first().toString()
                labels.add(monthName)
                val monthExpense = expenses
                    .filter { it.transaction.date in monthStart..monthEnd }
                    .sumOf { it.transaction.amount }
                data.add(monthExpense)
            }
            Pair(labels, data)
        }
        TimePeriod.ALL_TIME -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            for (i in 11 downTo 0) {
                calendar.time = now
                calendar.add(Calendar.MONTH, -i)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val monthStart = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val monthEnd = calendar.time
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(monthStart).first().toString()
                labels.add(monthName)
                val monthExpense = expenses
                    .filter { it.transaction.date in monthStart..monthEnd }
                    .sumOf { it.transaction.amount }
                data.add(monthExpense)
            }
            Pair(labels, data)
        }
    }
}
private fun calculateLineChartData(
    transactions: ImmutableList<TransactionWithCategory>,
    period: TimePeriod,
    selectedCategory: Category? = null
): Pair<List<String>, List<Double>> {
    val calendar = Calendar.getInstance()
    val now = calendar.time
    var expenses = transactions.filter { it.transaction.type == TransactionType.EXPENSE }
    if (selectedCategory != null) {
        expenses = expenses.filter { it.category.id == selectedCategory.id }
    }
    return when (period) {
        TimePeriod.THIS_WEEK -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            calendar.time = now
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val weekStart = calendar.time
            for (i in 0..6) {
                calendar.time = weekStart
                calendar.add(Calendar.DAY_OF_YEAR, i)
                val dayStart = calendar.time
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val dayEnd = calendar.time
                val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(dayStart)
                labels.add(dayName)
                val dayExpense = expenses
                    .filter { it.transaction.date in dayStart..dayEnd }
                    .sumOf { it.transaction.amount }
                data.add(dayExpense)
            }
            android.util.Log.d("ChartDebug", "THIS_WEEK line chart - data: $data, max=${data.maxOrNull()}, total=${data.sum()}, expenses count=${expenses.size}")
            expenses.forEach { 
                android.util.Log.d("ChartDebug", "  Transaction: date=${it.transaction.date}, amount=${it.transaction.amount}")
            }
            Pair(labels, data)
        }
        TimePeriod.THIS_MONTH -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            val currentTime = Date()
            val cal = Calendar.getInstance()
            cal.time = currentTime
            val lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val ranges = when (lastDayOfMonth) {
                28 -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 28)
                29 -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 29)
                30 -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 30)
                else -> listOf(1 to 7, 8 to 14, 15 to 21, 22 to 28, 29 to 31)
            }
            ranges.forEach { (startDay, endDay) ->
                cal.time = currentTime
                cal.set(Calendar.DAY_OF_MONTH, startDay)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val rangeStart = cal.time
                cal.time = currentTime
                cal.set(Calendar.DAY_OF_MONTH, endDay)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val rangeEnd = cal.time
                labels.add("$startDay-$endDay")
                val rangeExpense = expenses
                    .filter { it.transaction.date in rangeStart..rangeEnd }
                    .sumOf { it.transaction.amount }
                data.add(rangeExpense)
            }
            android.util.Log.d("ChartDebug", "THIS_MONTH line chart - data: $data, max=${data.maxOrNull()}, total=${data.sum()}, expenses count=${expenses.size}")
            Pair(labels, data)
        }
        TimePeriod.LAST_MONTH -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            for (i in 5 downTo 0) {
                calendar.time = now
                calendar.add(Calendar.MONTH, -i)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val monthStart = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val monthEnd = calendar.time
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(monthStart)
                labels.add(monthName)
                val monthExpense = expenses
                    .filter { it.transaction.date in monthStart..monthEnd }
                    .sumOf { it.transaction.amount }
                data.add(monthExpense)
            }
            Pair(labels, data)
        }
        TimePeriod.THIS_YEAR -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            for (i in 11 downTo 0) {
                calendar.time = now
                calendar.add(Calendar.MONTH, -i)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val monthStart = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val monthEnd = calendar.time
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(monthStart).first().toString()
                labels.add(monthName)
                val monthExpense = expenses
                    .filter { it.transaction.date in monthStart..monthEnd }
                    .sumOf { it.transaction.amount }
                data.add(monthExpense)
            }
            Pair(labels, data)
        }
        TimePeriod.ALL_TIME -> {
            val labels = mutableListOf<String>()
            val data = mutableListOf<Double>()
            for (i in 11 downTo 0) {
                calendar.time = now
                calendar.add(Calendar.MONTH, -i)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val monthStart = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.MILLISECOND, -1)
                val monthEnd = calendar.time
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(monthStart).first().toString()
                labels.add(monthName)
                val monthExpense = expenses
                    .filter { it.transaction.date in monthStart..monthEnd }
                    .sumOf { it.transaction.amount }
                data.add(monthExpense)
            }
            Pair(labels, data)
        }
    }
}
@Composable
fun LineChartSection(
    transactions: ImmutableList<TransactionWithCategory>,
    selectedPeriod: TimePeriod,
    currencyFormatter: NumberFormat,
    selectedCategory: Category? = null,
    onTrackerValueChange: (Double?, String?) -> Unit = { _, _ -> }
) {
    val (labels, data) = remember(transactions, selectedPeriod, selectedCategory) {
        calculateLineChartData(transactions, selectedPeriod, selectedCategory)
    }
    val minValue = data.minOrNull() ?: 0.0
    val maxValue = data.maxOrNull() ?: 0.0
    val range = maxValue - minValue
    val chartMin = if (range > 0) (minValue - range * 0.1).coerceAtLeast(0.0) else 0.0
    val chartMax = if (range > 0) maxValue + range * 0.1 else 1.0
    val chartRange = chartMax - chartMin
    val midValue = (chartMin + chartMax) / 2
    val lineColor = MaterialTheme.colorScheme.onSurface
    val pointColor = MaterialTheme.colorScheme.onSurface
    val gridLineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val trackerLineColor = MaterialTheme.colorScheme.primary
    var trackerIndex by remember { mutableIntStateOf(-1) }
    var isTrackerActive by remember { mutableStateOf(false) }
    var isTrackerReady by remember { mutableStateOf(false) }
    var lastTrackerIndex by remember { mutableIntStateOf(-1) }
    val animatedTrackerIndex = remember { Animatable(0f) }
    LaunchedEffect(trackerIndex, isTrackerActive) {
        if (isTrackerActive && trackerIndex >= 0) {
            if (lastTrackerIndex == -1 || !isTrackerReady) {
                animatedTrackerIndex.snapTo(trackerIndex.toFloat())
                isTrackerReady = true
            } else {
                animatedTrackerIndex.animateTo(
                    targetValue = trackerIndex.toFloat(),
                    animationSpec = tween(durationMillis = 150)
                )
            }
            lastTrackerIndex = trackerIndex
        } else if (!isTrackerActive) {
            isTrackerReady = false
            lastTrackerIndex = -1
        }
    }
    LaunchedEffect(transactions, selectedPeriod, selectedCategory) {
        trackerIndex = -1
        isTrackerActive = false
        isTrackerReady = false
        lastTrackerIndex = -1
        onTrackerValueChange(null, null)
    }
    LaunchedEffect(isTrackerActive, trackerIndex) {
        if (isTrackerActive && trackerIndex >= 0 && trackerIndex < data.size) {
            onTrackerValueChange(data[trackerIndex], labels[trackerIndex])
        } else {
            onTrackerValueChange(null, null)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        var canvasWidthPx by remember { mutableFloatStateOf(0f) }
        var canvasHeightPx by remember { mutableFloatStateOf(0f) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 48.dp, top = 16.dp, bottom = 8.dp)
                    .onGloballyPositioned { coordinates ->
                        canvasWidthPx = coordinates.size.width.toFloat()
                        canvasHeightPx = coordinates.size.height.toFloat()
                    }
                    .pointerInput(labels.size, data) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val longPressTimeout = 300L
                            val longPressResult = withTimeoutOrNull(longPressTimeout) {
                                waitForUpOrCancellation()
                            }
                            if (longPressResult == null) {
                                val totalPoints = data.size
                                if (totalPoints > 1 && canvasWidthPx > 0f) {
                                    val stepX = canvasWidthPx / (totalPoints - 1)
                                    val touchX = down.position.x
                                    var closestIndex = 0
                                    var minDistance = Float.MAX_VALUE
                                    for (i in 0 until totalPoints) {
                                        val pointX = i * stepX
                                        val distance = kotlin.math.abs(touchX - pointX)
                                        if (distance < minDistance) {
                                            minDistance = distance
                                            closestIndex = i
                                        }
                                    }
                                    trackerIndex = closestIndex
                                    isTrackerActive = true
                                    drag(down.id) { change ->
                                        change.consume()
                                        val dragTouchX = change.position.x
                                        var newClosestIndex = 0
                                        var newMinDistance = Float.MAX_VALUE
                                        for (i in 0 until totalPoints) {
                                            val pointX = i * stepX
                                            val distance = kotlin.math.abs(dragTouchX - pointX)
                                            if (distance < newMinDistance) {
                                                newMinDistance = distance
                                                newClosestIndex = i
                                            }
                                        }
                                        trackerIndex = newClosestIndex
                                    }
                                    isTrackerActive = false
                                    trackerIndex = -1
                                }
                            }
                        }
                    }
            ) {
                if (data.isEmpty() || data.size < 2) return@Canvas
                val stepX = size.width / (data.size - 1)
                listOf(0f, 0.5f, 1f).forEach { ratio ->
                    val y = size.height * (1f - ratio)
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }
                val linePath = Path()
                data.forEachIndexed { index, value ->
                    val x = index * stepX
                    val normalizedY = if (chartRange > 0) ((value - chartMin) / chartRange).toFloat() else 0.5f
                    val y = size.height - (normalizedY * size.height)
                    if (index == 0) {
                        linePath.moveTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                    }
                }
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                data.forEachIndexed { index, value ->
                    val x = index * stepX
                    val normalizedY = if (chartRange > 0) ((value - chartMin) / chartRange).toFloat() else 0.5f
                    val y = size.height - (normalizedY * size.height)
                    drawCircle(
                        color = pointColor,
                        radius = 5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
                if (isTrackerActive && isTrackerReady && trackerIndex >= 0 && trackerIndex < data.size) {
                    val animatedIdx = animatedTrackerIndex.value.coerceIn(0f, (data.size - 1).toFloat())
                    val lowerIdx = animatedIdx.toInt().coerceIn(0, data.size - 1)
                    val upperIdx = (lowerIdx + 1).coerceIn(0, data.size - 1)
                    val fraction = animatedIdx - lowerIdx
                    val lowerValue = data[lowerIdx]
                    val upperValue = data[upperIdx]
                    val interpolatedValue = lowerValue + (upperValue - lowerValue) * fraction
                    val trackerX = animatedIdx * stepX
                    val normalizedY = if (chartRange > 0) ((interpolatedValue - chartMin) / chartRange).toFloat() else 0.5f
                    val trackerY = size.height - (normalizedY * size.height)
                    val circleRadius = 8.dp.toPx()
                    drawLine(
                        color = trackerLineColor,
                        start = Offset(trackerX, 0f),
                        end = Offset(trackerX, trackerY - circleRadius),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawCircle(
                        color = trackerLineColor,
                        radius = circleRadius,
                        center = Offset(trackerX, trackerY),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White,
                        radius = circleRadius - 1.dp.toPx(),
                        center = Offset(trackerX, trackerY)
                    )
                }
            }
            val density = LocalDensity.current
            if (canvasHeightPx > 0) {
                listOf(
                    chartMax to 0f,
                    midValue to 0.5f,
                    chartMin to 1f
                ).forEach { (value, ratio) ->
                    val yPosition = 16.dp + with(density) { (canvasHeightPx * ratio).toDp() }
                    Text(
                        text = value.roundToInt().toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = yPosition - 8.dp)
                            .padding(end = 8.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEachIndexed { index, label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isTrackerActive && index == trackerIndex) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isTrackerActive && index == trackerIndex) 
                        FontWeight.Bold 
                    else 
                        FontWeight.Normal
                )
            }
        }
    }
}
@Composable
fun DonutChartSection(
    total: Double,
    categories: List<Pair<Category, Double>>,
    currencyFormatter: NumberFormat,
    selectedPeriod: TimePeriod,
    selectedCategory: Category? = null,
    onCategorySelected: (Category) -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    LaunchedEffect(categories, total) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(280.dp)
                .pointerInput(categories, total, selectedCategory) {
                    detectTapGestures { offset ->
                        if (selectedCategory != null) {
                            onCategorySelected(selectedCategory)
                            return@detectTapGestures
                        }
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val dx = offset.x - centerX
                        val dy = offset.y - centerY
                        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                        if (angle < 0) angle += 360
                        var relativeAngle = angle + 90
                        if (relativeAngle < 0) relativeAngle += 360
                        if (relativeAngle >= 360) relativeAngle -= 360
                        var currentAngle = 0f
                        categories.take(4).forEach { (category, amount) ->
                            val sweepAngle = ((amount / total) * 360f).toFloat()
                            if (relativeAngle >= currentAngle && relativeAngle < currentAngle + sweepAngle) {
                                onCategorySelected(category)
                                return@detectTapGestures
                            }
                            currentAngle += sweepAngle
                        }
                    }
                }
        ) {
            val strokeWidth = 40.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
        val centerX = size.width / 2
        val centerY = size.height / 2
        var startAngle = -90f
        if (selectedCategory != null) {
            val selectedIndex = categories.take(4).indexOfFirst { it.first.id == selectedCategory.id }
            val selectedCategoryData = categories.firstOrNull { it.first.id == selectedCategory.id }
            if (selectedCategoryData != null && selectedIndex >= 0) {
                val (_, amount) = selectedCategoryData
                val sweepAngle = ((amount / total) * 360f * animatedProgress.value).toFloat()
                val color = when (selectedIndex) {
                    0 -> primaryColor
                    1 -> secondaryColor
                    2 -> tertiaryColor
                    3 -> errorColor
                    else -> primaryColor
                }
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )
            }
        } else {
            categories.take(4).forEachIndexed { index, (_, amount) ->
                val sweepAngle = ((amount / total) * 360f * animatedProgress.value).toFloat()
                val color = when (index) {
                    0 -> primaryColor
                    1 -> secondaryColor
                    2 -> tertiaryColor
                    3 -> errorColor
                    else -> surfaceVariantColor
                }
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2)
                )
                startAngle += sweepAngle
            }
        }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                text = "Spent",
                style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                text = currencyFormatter.format(total),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                text = getPeriodDescription(selectedPeriod),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
fun PeriodSelectorRow(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit
) {
            Row(
                modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val periods = listOf(
            TimePeriod.THIS_WEEK to "1W",
            TimePeriod.THIS_MONTH to "1M",
            TimePeriod.LAST_MONTH to "6M",
            TimePeriod.THIS_YEAR to "1Y"
        )
        periods.forEach { (period, label) ->
            PeriodChip(
                label = label,
                isSelected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
fun PeriodChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) 
            MaterialTheme.colorScheme.primary 
        else 
            MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 0.dp else 0.dp,
        shadowElevation = if (isSelected) 0.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
fun CategoryBreakdownSection(
    categories: List<Pair<Category, Double>>,
    total: Double,
    currencyFormatter: NumberFormat,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "By category",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (selectedCategory != null) {
                TextButton(onClick = { onCategorySelected(selectedCategory) }) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        categories.take(5).forEachIndexed { index, (category, amount) ->
            CategoryItem(
                category = category,
                amount = amount,
                percentage = if (total > 0) (amount / total * 100) else 0.0,
                currencyFormatter = currencyFormatter,
                isSelected = selectedCategory?.id == category.id,
                onClick = { onCategorySelected(category) }
            )
            if (index < categories.take(5).size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
@Composable
fun CategoryItem(
    category: Category,
    amount: Double,
    percentage: Double,
    currencyFormatter: NumberFormat,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) 
            MaterialTheme.colorScheme.primaryContainer 
        else 
            Color.Transparent,
        tonalElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
        Box(
            modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(category.name),
                    contentDescription = "Category icon for ${category.name}",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "1 transaction", 
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "-${currencyFormatter.format(amount)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        }
    }
}
private fun getPeriodDescription(period: TimePeriod): String {
    return when (period) {
        TimePeriod.THIS_WEEK -> "This week"
        TimePeriod.THIS_MONTH -> "This month"
        TimePeriod.LAST_MONTH -> "Last 6 months"
        TimePeriod.THIS_YEAR -> "This year"
        TimePeriod.ALL_TIME -> "All time"
    }
}
private fun getFullTrackerLabel(shortLabel: String, period: TimePeriod): String {
    return when (period) {
        TimePeriod.THIS_WEEK -> {
            when (shortLabel.lowercase()) {
                "sun" -> "Sunday"
                "mon" -> "Monday"
                "tue" -> "Tuesday"
                "wed" -> "Wednesday"
                "thu" -> "Thursday"
                "fri" -> "Friday"
                "sat" -> "Saturday"
                else -> shortLabel
            }
        }
        TimePeriod.THIS_MONTH -> {
            val calendar = Calendar.getInstance()
            SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        }
        TimePeriod.LAST_MONTH -> {
            when (shortLabel.lowercase()) {
                "jan" -> "January"
                "feb" -> "February"
                "mar" -> "March"
                "apr" -> "April"
                "may" -> "May"
                "jun" -> "June"
                "jul" -> "July"
                "aug" -> "August"
                "sep" -> "September"
                "oct" -> "October"
                "nov" -> "November"
                "dec" -> "December"
                else -> shortLabel
            }
        }
        TimePeriod.THIS_YEAR -> {
            when (shortLabel.uppercase()) {
                "J" -> {
                    val calendar = Calendar.getInstance()
                    val currentMonth = calendar.get(Calendar.MONTH)
                    if (currentMonth <= Calendar.JUNE) "January" else "July"
                }
                "F" -> "February"
                "M" -> {
                    val calendar = Calendar.getInstance()
                    val currentMonth = calendar.get(Calendar.MONTH)
                    if (currentMonth <= Calendar.MAY) "March" else "May"
                }
                "A" -> {
                    val calendar = Calendar.getInstance()
                    val currentMonth = calendar.get(Calendar.MONTH)
                    if (currentMonth <= Calendar.JULY) "April" else "August"
                }
                "S" -> "September"
                "O" -> "October"
                "N" -> "November"
                "D" -> "December"
                else -> shortLabel
            }
        }
        TimePeriod.ALL_TIME -> shortLabel
    }
}