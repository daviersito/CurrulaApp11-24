package com.example.curruaapp.api

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor de OkHttp que añade el token de autenticación a las cabeceras
 * de todas las peticiones de red salientes.
 *
 * @param tokenProvider Una función lambda que proporciona el token de autenticación actual.
 */
class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {

    /**
     * Método que se ejecuta por cada petición de red.
     * Intercepta la petición original, le añade la cabecera "Authorization" con el token
     * si está disponible, y luego continúa con la petición modificada.
     *
     * @param chain La cadena de interceptores de OkHttp.
     * @return La respuesta del servidor.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtiene la petición original.
        val original = chain.request()
        // Obtiene el token usando la función lambda proporcionada.
        val token = tokenProvider()

        // Si el token no es nulo ni está en blanco, construye una nueva petición con la cabecera de autorización.
        val req = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            // Si no hay token, usa la petición original sin modificar.
            original
        }
        // Procede con la petición (original o modificada) y devuelve la respuesta.
        return chain.proceed(req)
    }
}
