package com.example.enqueteapp.presentation.poll_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enqueteapp.data.PollRepository
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

    fun createEnquete(title: String, options: List<String>) {
        viewModelScope.launch {
            try {
                val validOptions = options.filter { it.isNotBlank() }
                if (title.isBlank() || validOptions.size < 2) {
                    // ...
                    return@launch
                }
                // A chamada agora é mais simples
                val newPoll = withContext(Dispatchers.IO) {
                    repository.createEnquete(title, validOptions)
                }
                _formEvent.emit(FormEvent.Success(newPoll.id))
            } catch (e: Exception) {
                // ...
            }
        }
    }
}