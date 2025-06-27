package com.example.app.presentation.poll_list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.Enquete
import com.example.app.data.PollRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

sealed interface PollListUiState {
    object Loading : PollListUiState
    data class Success(val enquetes: List<Enquete>) : PollListUiState
    data class Error(val message: String) : PollListUiState
}

class PollListViewModel(

    private val repository: PollRepository = PollRepository()) : ViewModel() {

        private val _uiState = MutableStateFlow<PollListUiState>(PollListUiState.Loading)
        val uiState: StateFlow<PollListUiState> = _uiState

        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing: StateFlow<Boolean> = _isRefreshing

        fun fetchEnquetes() {
            _uiState.value = PollListUiState.Loading
            viewModelScope.launch {
                try {
                    val enquetes = withContext(Dispatchers.IO) {
                        repository.getEnquetes()
                            .sortedByDescending { it.data_criacao }
                    }
                    _uiState.value = PollListUiState.Success(enquetes)
                } catch (e: Exception) {
                    _uiState.value = PollListUiState.Error(e.message ?: "Erro desconhecido")
                } finally {
                    _isRefreshing.value = false
                }
            }
        }

        fun refresh() {
            _isRefreshing.value = true
            fetchEnquetes()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun isEnqueteExpirada(expiresAt: String?): Boolean {
            return try {
                val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                val expiration = OffsetDateTime.parse(expiresAt ?: return true, formatter)
                OffsetDateTime.now(ZoneOffset.UTC).isAfter(expiration)
            } catch (e: Exception) {
                true
            }
        }
}