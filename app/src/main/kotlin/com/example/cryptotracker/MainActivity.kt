package com.example.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // Keep for preview if needed, but CryptoTrackerTheme is primary
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cryptotracker.ui.screens.listings.CoinListScreen
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme // Import the new theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoTrackerTheme { // Use the new application theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // MaterialTheme can be used here to get scheme
                ) {
                    CoinListScreen() // Display the new screen
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CryptoTrackerTheme { // Use the new application theme for preview
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Previewing CoinListScreen directly.
            // For a more robust preview, you might want to pass a mock ViewModel
            // or use a simpler Composable if CoinListScreen has complex dependencies.
            CoinListScreen()
        }
    }
}
