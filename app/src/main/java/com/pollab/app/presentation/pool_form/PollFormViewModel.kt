package com.example.app.presentation.poll_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.PollRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface FormEvent {
    data class Success(val newPollId: Int) : FormEvent
    data class Error(val message: String) : FormEvent
}

class PollFormViewModel(private val repository: PollRepository = PollRepository()) : ViewModel() {

    private val _formEvent = MutableSharedFlow<FormEvent>()
    val formEvent = _formEvent.asSharedFlow()

    fun createEnquete(title: String, options: List<String>, duracaoHoras: Int) {
        viewModelScope.launch {
            try {
                val validOptions = options.filter { it.isNotBlank() }

                if (title.isBlank() || validOptions.size < 2) {
                    _formEvent.emit(FormEvent.Error("Título e pelo menos duas opções são obrigatórios."))
                    return@launch
                }

                if (duracaoHoras <= 0) {
                    _formEvent.emit(FormEvent.Error("Informe uma duração válida (maior que 0)."))
                    return@launch
                }

                val newPoll = withContext(Dispatchers.IO) {
                    repository.createEnquete(
                        title = title,
                        options = validOptions,
                        duracaoHoras = duracaoHoras
                    )
                }

                _formEvent.emit(FormEvent.Success(newPoll.id))

            } catch (e: Exception) {
                _formEvent.emit(FormEvent.Error("Erro ao criar enquete: ${e.localizedMessage}"))
            }
        }
    }
}