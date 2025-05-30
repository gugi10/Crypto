package com.example.cryptotracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    // Publicly expose coins as StateFlow (immutable)
    // val coins: StateFlow<List<Coin>> = _coins // Will be replaced by combined state if we want to hide list during refresh

    private val _isLoadingFromRefresh = MutableStateFlow(false)
    // val isRefreshing: StateFlow<Boolean> = _isLoadingFromRefresh // Expose if needed separately

    // isLoading is true if we are actively refreshing OR if the initial coin list is empty
    // This means shimmer will show on first load, and potentially during manual refresh if list becomes empty
    val isLoading: StateFlow<Boolean> = combine(_coins, _isLoadingFromRefresh) { coins, refreshing ->
        refreshing || coins.isEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true) // Initial value true to show loading

    // Expose coins directly. The UI will decide to show shimmer based on isLoading and if coins is empty.
    val coins: StateFlow<List<Coin>> = _coins.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadCoinsFromDb() // Start collecting from DB
        refreshCoinsData() // Trigger initial fetch from network
    }

    private fun loadCoinsFromDb() {
        viewModelScope.launch {
            coinRepository.getAllCoins().collect {
                _coins.value = it
            }
        }
    }

    fun refreshCoinsData() { // Made public for swipe-to-refresh scenarios
        viewModelScope.launch {
            _isLoadingFromRefresh.value = true
            try {
                coinRepository.refreshCoins() // vsCurrency defaults to "usd" in repository
            } catch (e: Exception) {
                // Handle error (e.g., show a snackbar or log)
                // For now, error state is not explicitly handled here, but isLoadingFromRefresh will be false
                println("Error refreshing coins: ${e.message}")
            } finally {
                _isLoadingFromRefresh.value = false
            }
        }
    }
}
