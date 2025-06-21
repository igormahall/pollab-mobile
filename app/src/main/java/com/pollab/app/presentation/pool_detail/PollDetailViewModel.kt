package com.example.app.presentation.poll_detail

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

// Representa eventos únicos, como mostrar uma notificação de erro
sealed interface VoteEvent {
    data class ShowToast(val message: String) : VoteEvent
}

sealed interface PollDetailUiState {
    object Loading : PollDetailUiState
    // CORREÇÃO 1: Adicionar a propriedade isVoting de volta
    data class Success(val enquete: Enquete, val isVoting: Boolean = false) : PollDetailUiState
    data class Error(val message: String) : PollDetailUiState
}

class PollDetailViewModel(private val repository: PollRepository = PollRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<PollDetailUiState>(PollDetailUiState.Loading)
    val uiState: StateFlow<PollDetailUiState> = _uiState

    private val _voteEvent = MutableSharedFlow<VoteEvent>()
    val voteEvent = _voteEvent.asSharedFlow()

    fun fetchEnquete(pollId: String) {
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

    // CORREÇÃO 2: Lógica de voto atualizada para gerenciar o estado 'isVoting'
    fun vote(optionId: Int, participantId: String) {
        val currentState = _uiState.value
        // Garante que só podemos votar se o estado for Success e não estivermos já votando
        if (currentState is PollDetailUiState.Success && !currentState.isVoting) {
            // Atualiza o estado para mostrar que a votação começou
            _uiState.value = currentState.copy(isVoting = true)

            viewModelScope.launch {
                val pollId = currentState.enquete.id.toString()
                try {
                    withContext(Dispatchers.IO) {
                        repository.vote(pollId, optionId, participantId)
                    }
                    // Sucesso! Recarrega a enquete para obter os dados mais recentes.
                    // Isso irá automaticamente definir o novo estado de Success com isVoting = false.
                    fetchEnquete(pollId)
                } catch (e: Exception) {
                    // Erro! Emite um evento de notificação e reseta o estado de votação.
                    _voteEvent.emit(VoteEvent.ShowToast("Não foi possível registrar o voto. Você já votou nesta enquete?"))
                    _uiState.value = currentState.copy(isVoting = false)
                }
            }
        }
    }
}