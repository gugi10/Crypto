package com.example.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.cryptotracker.ui.AppNavHost // Import AppNavHost
import com.example.cryptotracker.ui.screens.listings.CoinListScreen // Keep for preview if needed
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoTrackerTheme {
                val navController = rememberNavController() // Create NavController
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(navController = navController) // Set up NavHost
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CryptoTrackerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // For previewing specific screens that might need a NavController or are part of navigation,
            // you might call them directly or wrap with a preview-specific NavController.
            // Here, previewing CoinListScreen as it was. AppNavHost itself is harder to preview directly with all routes.
            CoinListScreen(onCoinClick = {}) // Pass an empty lambda for onCoinClick in preview
        }
    }
}
