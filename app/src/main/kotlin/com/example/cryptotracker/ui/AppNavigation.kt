package com.example.cryptotracker.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cryptotracker.ui.screens.detail.CoinDetailScreen // Import new screen
import com.example.cryptotracker.ui.screens.listings.CoinListScreen

sealed class Screen(val route: String) {
    object CoinList : Screen("coin_list")
    object CoinDetail : Screen("coin_detail/{coinId}") {
        fun createRoute(coinId: String) = "coin_detail/$coinId"
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.CoinList.route) {
        composable(Screen.CoinList.route) {
            CoinListScreen(
                onCoinClick = { coinId ->
                    navController.navigate(Screen.CoinDetail.createRoute(coinId))
                }
            )
        }
        composable(
            route = Screen.CoinDetail.route,
            arguments = listOf(navArgument("coinId") { type = NavType.StringType })
        ) { backStackEntry ->
            val coinId = backStackEntry.arguments?.getString("coinId")
            CoinDetailScreen( // Call the actual screen
                coinId = coinId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
