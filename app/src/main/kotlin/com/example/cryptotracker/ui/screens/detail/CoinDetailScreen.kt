package com.example.cryptotracker.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptotracker.ui.viewmodel.CoinDetailViewModel
import com.example.cryptotracker.ui.viewmodel.CoinDetailUiState
import com.example.cryptotracker.data.model.Coin // Import Coin model
import com.example.cryptotracker.data.model.MarketChart // Import MarketChart model
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.automirrored.filled.ArrowBack 
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Local version of formatCurrencyValue to avoid dependency/visibility issues for this subtask
fun formatCurrencyValue(price: Double?, locale: Locale = Locale.US): String {
    if (price == null) return "N/A"
    return NumberFormat.getCurrencyInstance(locale).apply {
         maximumFractionDigits = if (price >= 1) 2 else if (price >= 0.00001) 6 else 8
         minimumFractionDigits = 2
    }.format(price)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinId: String?, // Passed from navigation
    viewModel: CoinDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(uiState.coin?.name ?: coinId ?: "Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // Overall loading state, primarily for when coin detail itself isn't loaded
            if (uiState.isLoadingCoinDetail && uiState.coin == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null && uiState.coin == null) { // Show error if coin details failed and we have no coin info
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (uiState.coin == null) { // Fallback if not loading and no error, but coin is still null
                Text(
                    text = "Coin data not available for ID: $coinId",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                // Coin details are available, show content
                CoinDetailContent(uiState = uiState)
            }
        }
    }
}

@Composable
fun CoinDetailContent(uiState: CoinDetailUiState) {
    val coin = uiState.coin!! // uiState.coin is confirmed non-null by the caller logic

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Coin Header (Name, Symbol)
        Text(text = "${coin.name} (${coin.symbol.uppercase()})", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Current Price: ${formatCurrencyValue(coin.currentPrice)}", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(24.dp))

        // Market Chart Section
        Text("Market Chart (7 days)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isLoadingMarketChart) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 50.dp))
        } else if (uiState.marketChart != null && uiState.marketChart.prices.isNotEmpty()) {
            MarketChartView(marketChart = uiState.marketChart)
        } else if (uiState.error != null && uiState.marketChart == null) { // Show error related to chart if it occurred
             Text("Could not load chart: ${uiState.error}")
        }
        else {
            Text("Chart data is not available.")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Additional Info Section
        Text("Additional Details", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Text("Market Cap: ${formatCurrencyValue(coin.marketCap?.toDouble())}", style = MaterialTheme.typography.bodyLarge)
        Text("Total Volume (24h): ${formatCurrencyValue(coin.totalVolume)}", style = MaterialTheme.typography.bodyLarge)
        coin.priceChangePercentage24h?.let {
            Text("Price Change (24h): ${String.format(Locale.US, "%.2f%%", it)}",
                color = if (it >= 0) Color(0xFF009688) else Color(0xFFE53935),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        coin.ath?.let { Text("All-Time High: ${formatCurrencyValue(it)}", style = MaterialTheme.typography.bodyMedium) }
        coin.atl?.let { Text("All-Time Low: ${formatCurrencyValue(it)}", style = MaterialTheme.typography.bodyMedium) }
        coin.circulatingSupply?.let { Text("Circulating Supply: ${NumberFormat.getNumberInstance(Locale.US).format(it)}", style = MaterialTheme.typography.bodyMedium) }
        coin.totalSupply?.let { Text("Total Supply: ${NumberFormat.getNumberInstance(Locale.US).format(it)}", style = MaterialTheme.typography.bodyMedium) }
        coin.lastUpdated?.let { Text("Last Updated: $it", style = MaterialTheme.typography.bodySmall) }

    }
}

@Composable
fun MarketChartView(marketChart: MarketChart) { // Explicitly use the model from data.model
    val points = marketChart.prices.mapIndexedNotNull { _, pricePoint ->
        Point(pricePoint.timestamp.toFloat(), pricePoint.value.toFloat())
    }

    if (points.size < 2) {
        Text("Not enough data for chart. (${points.size} points found)")
        return
    }
    
    val stepSize = (points.size / 7).coerceAtLeast(1)
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp) 
        .backgroundColor(MaterialTheme.colorScheme.surface)
        .steps(points.size - 1) 
        .labelData { index ->
             if (index % stepSize == 0 && index < marketChart.prices.size) {
                dateFormat.format(Date(marketChart.prices[index].timestamp))
             } else ""
        }
        .labelAndAxisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .build()

    val minYValue = points.minOfOrNull { it.y } ?: 0f
    val maxYValue = points.maxOfOrNull { it.y } ?: 1f
    
    val yAxisData = AxisData.Builder()
        .steps(4) 
        .backgroundColor(MaterialTheme.colorScheme.surface)
        .labelAndAxisLineColor(MaterialTheme.colorScheme.onSurfaceVariant)
        .labelData { value ->
            val price = value.toDouble()
            String.format(Locale.US, "$%.2f", price) 
        }
        // Pass min and max to YCharts if it supports it, or calculate appropriate range.
        // For YCharts, it seems to auto-scale based on data, but explicit control might be needed for some cases.
        // .yMaxValue(maxYValue) // Check YCharts API if direct setting is available/needed
        // .yMinValue(minYValue)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points,
                    lineStyle = LineStyle(color = MaterialTheme.colorScheme.primary, strokeWidth = 3f),
                    intersectionPoint = IntersectionPoint(color = MaterialTheme.colorScheme.primary, radius = 4.dp),
                    selectionHighlightPoint = SelectionHighlightPoint(color = MaterialTheme.colorScheme.tertiary),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.3f,
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp(
                        backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        popOffSet = (-12).dp, 
                        labelFormatter = { point ->
                            val price = point.y.toDouble()
                            val time = marketChart.prices.getOrNull(points.indexOf(point))?.timestamp
                            val dateStr = if(time != null) dateFormat.format(Date(time)) else ""
                            "${formatCurrencyValue(price)} on $dateStr"
                        }
                    )
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        paddingRight = 0.dp, 
        containerPaddingEnd = 8.dp 
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        lineChartData = lineChartData
    )
}

@Preview(showBackground = true, name = "Coin Detail Content Preview")
@Composable
fun CoinDetailContentPreview() {
    CryptoTrackerTheme {
        Surface {
            CoinDetailContent(
                uiState = CoinDetailUiState(
                    coin = Coin(
                        id = "bitcoin", name = "Bitcoin", symbol = "BTC", currentPrice = 45000.0,
                        image = "", marketCap = 900000000000L, marketCapRank = 1, totalVolume = 15000000000.0,
                        priceChangePercentage24h = 2.5, circulatingSupply = 19000000.0, totalSupply = 21000000.0,
                        maxSupply = 21000000.0, ath = 69000.0, athChangePercentage = -30.0, athDate = "",
                        atl = 3000.0, atlChangePercentage = 1400.0, atlDate = "", lastUpdated = "2023-10-27T10:00:00.000Z"
                    ),
                    marketChart = MarketChart(
                        prices = listOf(
                            com.example.cryptotracker.data.model.PricePoint(System.currentTimeMillis() - 2*24*60*60*1000L, 44000.0),
                            com.example.cryptotracker.data.model.PricePoint(System.currentTimeMillis() - 1*24*60*60*1000L, 44500.0),
                            com.example.cryptotracker.data.model.PricePoint(System.currentTimeMillis(), 45000.0)
                        ),
                        marketCaps = emptyList(), totalVolumes = emptyList()
                    ),
                    isLoadingCoinDetail = false, isLoadingMarketChart = false, error = null
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Coin Detail Screen Loading Coin Detail")
@Composable
fun CoinDetailScreenLoadingCoinPreview() {
    CryptoTrackerTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true, name = "Coin Detail Screen Loading Chart")
@Composable
fun CoinDetailScreenLoadingChartPreview() {
     CryptoTrackerTheme {
        Surface {
            CoinDetailContent( // Show coin details, but chart area shows loading
                 uiState = CoinDetailUiState(
                    coin = Coin(
                        id = "bitcoin", name = "Bitcoin", symbol = "BTC", currentPrice = 45000.0,
                        image = "", marketCap = 900000000000L, marketCapRank = 1, totalVolume = 15000000000.0,
                        priceChangePercentage24h = 2.5, circulatingSupply = 19000000.0, totalSupply = 21000000.0,
                        maxSupply = 21000000.0, ath = 69000.0, athChangePercentage = -30.0, athDate = "",
                        atl = 3000.0, atlChangePercentage = 1400.0, atlDate = "", lastUpdated = "2023-10-27T10:00:00.000Z"
                    ),
                    marketChart = null, // No chart data yet
                    isLoadingCoinDetail = false, isLoadingMarketChart = true, error = null
                )
            )
        }
    }
}


@Preview(showBackground = true, name = "Coin Detail Screen Error Preview")
@Composable
fun CoinDetailScreenErrorPreview() {
    CryptoTrackerTheme {
        Surface {
             Box(modifier = Modifier.padding(16.dp).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error: Could not load coin details.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
