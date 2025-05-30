package com.example.cryptotracker.ui.screens.listings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import com.example.cryptotracker.ui.viewmodel.CoinListViewModel
import com.valentinilk.shimmer.shimmer
import java.text.NumberFormat
import java.util.Locale

val shimmerBrush = Brush.horizontalGradient(
    colors = listOf(
        Color.LightGray.copy(alpha = 0.9f),
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.9f)
    )
)

@Composable
fun CoinListScreen(
    viewModel: CoinListViewModel = hiltViewModel()
) {
    val coins by viewModel.coins.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading && coins.isEmpty()) { // Show shimmer only if loading and list is currently empty
            LazyColumn(modifier = Modifier.fillMaxSize().shimmer()) {
                items(15) { // Show 15 placeholder items
                    CoinListItemPlaceholder(brush = shimmerBrush)
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        } else if (!isLoading && coins.isEmpty()) { // Show empty message if not loading and list is empty
            Text(
                text = "No coins found. Pull to refresh or check network.",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        } else { // Show the actual list of coins
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(coins) { coin ->
                    CoinListItem(coin = coin)
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun CoinListItemPlaceholder(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(brush)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(fraction = 0.7f)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(fraction = 0.4f)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(fraction = 0.3f)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(fraction = 0.2f)
                    .background(brush, shape = RoundedCornerShape(4.dp))
            )
        }
    }
}


@Composable
fun CoinListItem(coin: Coin) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = coin.image,
            contentDescription = "${coin.name} logo",
            modifier = Modifier.size(40.dp).clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = coin.name,
                fontWeight = FontWeight.Medium,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = coin.symbol.uppercase(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatCurrency(coin.currentPrice),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatPercentage(coin.priceChangePercentage24h),
                fontSize = 14.sp,
                color = if ((coin.priceChangePercentage24h ?: 0.0) >= 0) Color(0xFF009688) else Color(0xFFE53935) // Teal/Red
            )
        }
    }
}

// Helper functions
fun formatCurrency(price: Double?, locale: Locale = Locale.getDefault()): String {
    if (price == null) return "N/A"
    return NumberFormat.getCurrencyInstance(locale).apply {
         maximumFractionDigits = if (price >= 1) 2 else if (price >= 0.0001) 4 else 8
         minimumFractionDigits = 2
    }.format(price)
}

fun formatPercentage(percentage: Double?): String {
    if (percentage == null) return "N/A"
    return String.format(Locale.US, "%.2f%%", percentage)
}

@Preview(showBackground = true, name = "Coin List Item Preview")
@Composable
fun CoinListItemPreview() {
    CryptoTrackerTheme {
        CoinListItem(
            coin = Coin(
                id = "bitcoin",
                symbol = "btc",
                name = "Bitcoin",
                image = "https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1696501400",
                currentPrice = 34567.89,
                marketCap = 678901234567,
                marketCapRank = 1,
                totalVolume = 1234567890.0,
                priceChangePercentage24h = 2.34,
                circulatingSupply = 19000000.0,
                totalSupply = 21000000.0,
                maxSupply = 21000000.0,
                ath = 69045.0,
                athChangePercentage = -50.0,
                athDate = "2021-11-10T14:24:11.849Z",
                atl = 67.81,
                atlChangePercentage = 50987.0,
                atlDate = "2013-07-06T00:00:00.000Z",
                lastUpdated = "2023-10-27T10:00:00.000Z"
            )
        )
    }
}

@Preview(showBackground = true, name = "Coin List Item Placeholder Preview")
@Composable
fun CoinListItemPlaceholderPreview() {
    CryptoTrackerTheme {
        CoinListItemPlaceholder(brush = shimmerBrush)
    }
}

@Preview(showBackground = true, name = "Coin List Screen Shimmer Preview")
@Composable
fun CoinListScreenShimmerPreview() {
    CryptoTrackerTheme {
        LazyColumn(modifier = Modifier.fillMaxSize().shimmer()) {
            items(15) {
                CoinListItemPlaceholder(brush = shimmerBrush)
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Preview(showBackground = true, name = "Coin List Screen Empty State Preview")
@Composable
fun CoinListScreenEmptyStatePreview() {
    CryptoTrackerTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No coins found. Pull to refresh or check network.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true, name = "Coin List Screen With Data Preview")
@Composable
fun CoinListScreenWithDataPreview() {
    val previewCoins = listOf(
        Coin("bitcoin", "btc", "Bitcoin", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png?1696501400", 34567.89, 678901234567,1,1234567890.0, 2.34,19000000.0,21000000.0,21000000.0,69045.0, -50.0, "2021-11-10T14:24:11.849Z",67.81, 50987.0, "2013-07-06T00:00:00.000Z", "2023-10-27T10:00:00.000Z"),
        Coin("ethereum", "eth", "Ethereum", "https://assets.coingecko.com/coins/images/279/large/ethereum.png?1696501628", 1800.55, 216987654321, 2, 987654321.0, -0.55, 120000000.0,0.0,0.0,4878.0,-63.0,"2021-11-10T14:24:11.849Z",0.43,420000.0,"2015-10-20T00:00:00.000Z","2023-10-27T10:00:00.000Z")

    )
    CryptoTrackerTheme {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(previewCoins) { coin ->
                CoinListItem(coin = coin)
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}
