package com.pollab.app.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(private val maxRetries: Int = 3) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null

        var tryCount = 0
        while (response == null && tryCount < maxRetries) {
            try {
                // Tenta executar a requisição
                response = chain.proceed(request.newBuilder().build())
            } catch (e: IOException) {
                // Se falhar, guarda a exceção e incrementa o contador
                exception = e
                tryCount++

                // Se ainda temos tentativas, espera um pouco antes da próxima
                if(tryCount < maxRetries) {
                    try {
                        Thread.sleep(2000) // Espera 2 segundos
                    } catch (e: InterruptedException) {
                        Thread.currentThread().interrupt()
                    }
                }
            }
        }

        // Se todas as tentativas falharam, lança a última exceção
        if (response == null && exception != null) {
            throw exception
        }

        return response!!
    }
}