// src/main/java/com/example/enqueteapp/network/ApiService.kt
package com.example.enqueteapp.network

import com.example.enqueteapp.data.VotePayload
import com.example.enqueteapp.data.Enquete
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.enqueteapp.data.CreateEnquetePayload

// 1. Define os endpoints da nossa API
interface ApiService {
    @GET("api/enquetes/")
    suspend fun getEnquetes(): List<Enquete>

    // --- Adicione este novo método ---
    @GET("api/enquetes/{id}/")
    suspend fun getEnquete(@Path("id") pollId: String): Enquete

    @POST("api/enquetes/{id}/votar/")
    suspend fun vote(
        @Path("id") pollId: String,
        @Body payload: VotePayload
    ): Enquete // A API retorna a enquete atualizada

    @POST("api/enquetes/")
    suspend fun createEnquete(@Body payload: CreateEnquetePayload): Enquete
}

// 2. Cria um objeto singleton para instanciar o Retrofit
object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}