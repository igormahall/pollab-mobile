package com.example.app.presentation.poll_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.Enquete
import com.example.app.data.PollRepository
import kotlinx.coroutines.Dispatchers // 1. Importe Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // 2. Importe withContext

sealed interface PollListUiState {
    object Loading : PollListUiState
    data class Success(val enquetes: List<Enquete>) : PollListUiState
    data class Error(val message: String) : PollListUiState
}

class PollListViewModel(private val repository: PollRepository = PollRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<PollListUiState>(PollListUiState.Loading)
    val uiState: StateFlow<PollListUiState> = _uiState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun fetchEnquetes() {
        _uiState.value = PollListUiState.Loading
        viewModelScope.launch {
            try {
                // 3. A CHAMADA AO REPOSITÓRIO AGORA ESTÁ DENTRO DE withContext
                val enquetes = withContext(Dispatchers.IO) {
                    repository.getEnquetes()
                }
                _uiState.value = PollListUiState.Success(enquetes)
            } catch (e: Exception) {
                _uiState.value = PollListUiState.Error(e.message ?: "Erro desconhecido")
            } finally {
                // Garante que o indicador de refresh pare de girar
                _isRefreshing.value = false
            }
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        fetchEnquetes()
    }
}