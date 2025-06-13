package com.example.enqueteapp.presentation.poll_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enqueteapp.data.Enquete
import com.example.enqueteapp.data.PollRepository
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
            }
        }
    }
}