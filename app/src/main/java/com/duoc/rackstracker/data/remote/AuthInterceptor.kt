package com.duoc.rackstracker.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Añade el header Authorization: Bearer <token> a todas las peticiones.
 * El token se lee de forma diferida, así se puede iniciar sesión sin reconstruir Retrofit.
 */
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider()

        val request = if (token.isNullOrBlank()) {
            original
        } else {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(request)
    }
}
