package com.example.cryptotracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.repository.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinRepository: CoinRepository
) : ViewModel() {

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    val coins: StateFlow<List<Coin>> = _coins

    init {
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            coinRepository.getAllCoins().collect {
                _coins.value = it
            }
        }
        viewModelScope.launch {
            // Ensure this is called in a way that doesn't block UI or cause issues
            // For a real app, consider network availability and error handling more deeply
            coinRepository.refreshCoins()
        }
    }
}
