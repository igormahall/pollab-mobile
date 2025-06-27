package com.example.app.presentation.poll_detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.Enquete
import com.example.app.data.PollRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import retrofit2.HttpException

// Representa eventos únicos, como mostrar uma notificação de erro
sealed interface VoteEvent {
    data class ShowToast(val message: String) : VoteEvent
}

sealed interface PollDetailUiState {
    object Loading : PollDetailUiState
    data class Success(val enquete: Enquete, val isVoting: Boolean = false) : PollDetailUiState
    data class Error(val message: String) : PollDetailUiState
}

class PollDetailViewModel(private val repository: PollRepository = PollRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<PollDetailUiState>(PollDetailUiState.Loading)
    val uiState: StateFlow<PollDetailUiState> = _uiState

    private val _voteEvent = MutableSharedFlow<VoteEvent>()
    val voteEvent = _voteEvent.asSharedFlow()

    private var currentPollId: String? = null

    fun fetchEnquete(pollId: String) {
        currentPollId = pollId
        _uiState.value = PollDetailUiState.Loading
        viewModelScope.launch {
            try {
                val enquete = withContext(Dispatchers.IO) { repository.getEnquete(pollId) }
                _uiState.value = PollDetailUiState.Success(enquete)
            } catch (e: Exception) {
                _uiState.value = PollDetailUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun vote(optionId: Int, participantId: String) {
        val currentState = _uiState.value
        if (currentState is PollDetailUiState.Success && !currentState.isVoting) {
            _uiState.value = currentState.copy(isVoting = true)

            viewModelScope.launch {
                val pollId = currentPollId ?: return@launch
                try {
                    withContext(Dispatchers.IO) {
                        repository.vote(pollId, optionId, participantId)
                    }
                    fetchEnquete(pollId)
                } catch (e: Exception) {
                    val message = when (e) {
                        is HttpException -> {
                            when (e.code()) {
                                409 -> "Você já votou nesta enquete."
                                else -> "Erro ao registrar o voto: ${e.message()}"
                            }
                        }
                        else -> "Erro inesperado ao votar."
                    }
                    _voteEvent.emit(VoteEvent.ShowToast(message))
                    _uiState.value = currentState.copy(isVoting = false)
                }
            }
        }
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