package com.example.cryptotracker.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.MarketChart
import com.example.cryptotracker.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CoinDetailUiState(
    val coin: Coin? = null,
    val marketChart: MarketChart? = null,
    val isLoadingCoinDetail: Boolean = false,
    val isLoadingMarketChart: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = savedStateHandle.get<String>("coinId") ?: ""

    private val _uiState = MutableStateFlow(CoinDetailUiState())
    val uiState: StateFlow<CoinDetailUiState> = _uiState.asStateFlow()

    init {
        if (coinId.isNotBlank()) {
            _uiState.value = CoinDetailUiState(isLoadingCoinDetail = true, isLoadingMarketChart = true)
            fetchCoinDetails()
            fetchMarketChartData() // Default to 7 days
        } else {
            _uiState.value = CoinDetailUiState(isLoadingCoinDetail = false, isLoadingMarketChart = false, error = "Coin ID not found.")
        }
    }

    private fun fetchCoinDetails() {
        viewModelScope.launch {
            val currentError = _uiState.value.error?.takeUnless { it == "Coin ID not found." } // Preserve other errors
            _uiState.value = _uiState.value.copy(isLoadingCoinDetail = true, error = currentError)

            val coinFromCache = coinRepository.getAllCoins().firstOrNull()?.find { it.id == coinId }

            if (coinFromCache != null) {
                _uiState.value = _uiState.value.copy(coin = coinFromCache, isLoadingCoinDetail = false)
            } else {
                 _uiState.value = _uiState.value.copy(
                     isLoadingCoinDetail = false,
                     error = _uiState.value.error ?: "Coin details for '$coinId' not found in cache. Implement specific fetch."
                 )
            }
        }
    }

    fun fetchMarketChartData(days: String = "7") {
        if (coinId.isBlank()) {
            _uiState.value = _uiState.value.copy(isLoadingMarketChart = false, error = _uiState.value.error ?: "Cannot fetch chart: Coin ID is blank.")
            return
        }

        viewModelScope.launch {
            val existingError = _uiState.value.error?.takeIf { it != "Error fetching market chart." }
            _uiState.value = _uiState.value.copy(isLoadingMarketChart = true, error = existingError, marketChart = null)

            try {
                // Use the actual repository call
                val result = coinRepository.getMarketChartData(coinId = coinId, vsCurrency = "usd", days = days)

                result.fold(
                    onSuccess = { marketChartData ->
                        _uiState.value = _uiState.value.copy(
                            marketChart = marketChartData,
                            isLoadingMarketChart = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = _uiState.value.error ?: exception.message ?: "Error fetching market chart.",
                            isLoadingMarketChart = false,
                            marketChart = null
                        )
                    }
                )
            } catch (e: Exception) {
                 _uiState.value = _uiState.value.copy(
                    error = _uiState.value.error ?: e.message ?: "Unexpected error fetching market chart.",
                    isLoadingMarketChart = false,
                    marketChart = null
                )
            }
        }
    }
}
