package com.example.cryptotracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class) // For debounce and stateIn
@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Holds the full list of coins from the repository, fetched once and observed
    private val _allCoins: StateFlow<List<Coin>> = coinRepository.getAllCoins()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoadingFromRefresh = MutableStateFlow(false)
    private val _initialLoadAttempted = MutableStateFlow(false)
    
    // isLoading now primarily reflects the refresh operation's status,
    // or initial load if _allCoins is empty AND not yet successfully refreshed.
    val isLoading: StateFlow<Boolean> = combine(
        _allCoins, 
        _isLoadingFromRefresh,
        _initialLoadAttempted
    ) { allCoins, isRefreshing, initialLoadAttempted ->
        isRefreshing || (allCoins.isEmpty() && !initialLoadAttempted)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true) // Start with true


    // Filtered coins based on search query
    val coins: StateFlow<List<Coin>> = combine(
        _allCoins,
        _searchQuery.debounce(300L) // Debounce search query
    ) { allCoins, query ->
        if (query.isBlank()) {
            allCoins
        } else {
            allCoins.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.symbol.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Initial data refresh; getAllCoins is already being collected by _allCoins
        refreshCoinsData()
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun refreshCoinsData() {
        viewModelScope.launch {
            _isLoadingFromRefresh.value = true
            try {
                coinRepository.refreshCoins() // Default vsCurrency = "usd"
            } catch (e: Exception) {
                // Handle error (e.g., update an error StateFlow - not implemented in this VM yet)
                println("Error refreshing coins in ViewModel: ${e.message}")
            } finally {
                _isLoadingFromRefresh.value = false
                _initialLoadAttempted.value = true // Mark that an attempt has been made
            }
        }
    }
}
