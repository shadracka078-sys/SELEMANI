package com.example.selemani.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // Njia salama ya kubeba data za Dashboard bila App ku-crash
    private val _uiState = MutableStateFlow("Tayari")
    val uiState: StateFlow<String> = _uiState

    fun chukuaDataZaDashboard() {
        viewModelScope.launch(Dispatchers.IO) {
            // Hapa ndipo unaposoma Room Database kwenye Background Thread
        }
    }
}
