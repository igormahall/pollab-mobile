// src/main/java/com/example/enqueteapp/data/PollRepository.kt
package com.example.enqueteapp.data

import com.example.enqueteapp.network.ApiService
import com.example.enqueteapp.network.RetrofitInstance
import com.example.enqueteapp.data.CreateEnquetePayload

class PollRepository(private val apiService: ApiService = RetrofitInstance.api) {

    // Esta função busca as enquetes da API através do nosso ApiService.
    // Como a chamada de rede pode demorar, ela é uma 'suspend function'.
    suspend fun getEnquetes(): List<Enquete> {
        return apiService.getEnquetes()
    }

    suspend fun getEnquete(pollId: String): Enquete {
        return apiService.getEnquete(pollId)
    }

    suspend fun vote(pollId: String, optionId: Int, participantId: String): Enquete {
        val payload = VotePayload(id_opcao = optionId, id_participante = participantId)
        return apiService.vote(pollId, payload)
    }

    suspend fun createEnquete(title: String, options: List<String>): Enquete {
        val payload = CreateEnquetePayload(titulo = title, opcoes_input = options)
        return apiService.createEnquete(payload)
    }
}