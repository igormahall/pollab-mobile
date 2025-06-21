// src/main/java/com/example/enqueteapp/data/PollModels.kt
package com.example.app.data

data class Opcao(
    val id: Int,
    val texto_opcao: String,
    val votos: Int
)

data class Enquete(
    val id: Int,
    val titulo: String,
    val data_criacao: String,
    val status: String,
    val opcoes: List<Opcao>
)

data class CreateEnquetePayload(
    val titulo: String,
    val opcoes_input: List<String>
)