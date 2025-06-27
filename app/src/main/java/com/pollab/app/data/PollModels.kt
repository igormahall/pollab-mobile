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
    val expires_at: String?,
    val delete_at: String?,
    val status: String,
    val opcoes: List<Opcao>
)

data class CreateEnquetePayload(
    val titulo: String,
    val duracao_horas: Int,
    val opcoes_input: List<String>
)