package com.example.app.data

import com.example.app.network.ApiService
import com.example.app.network.RetrofitInstance

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

    suspend fun createEnquete(title: String, options: List<String>, duracaoHoras: Int): Enquete {
        val payload = CreateEnquetePayload(
            titulo = title,
            opcoes_input = options,
            duracao_horas = duracaoHoras
        )
        return apiService.createEnquete(payload)
    }
}