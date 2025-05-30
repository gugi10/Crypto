package com.example.cryptotracker.ui.screens.listings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.ui.viewmodel.CoinListViewModel

@Composable
fun CoinListScreen(
    viewModel: CoinListViewModel = hiltViewModel()
) {
    val coins by viewModel.coins.collectAsState()
    // A simple state for loading, could be more sophisticated
    val isLoading = coins.isEmpty() // Consider a dedicated isLoading state in ViewModel

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(coins) { coin ->
                    CoinRow(coin = coin)
                }
            }
        }
    }
}

@Composable
fun CoinRow(coin: Coin, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(text = coin.name)
        Text(text = coin.symbol.uppercase())
    }
}

@Preview(showBackground = true)
@Composable
fun CoinListScreenPreview() {
    // This preview won't work well without a mock ViewModel
    // For now, just showing a simple list
    val previewCoins = listOf(
        Coin("bitcoin", "Bitcoin", "btc"),
        Coin("ethereum", "Ethereum", "eth")
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(previewCoins) { coin ->
            CoinRow(coin = coin)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoinListScreenLoadingPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
