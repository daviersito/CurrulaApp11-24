package com.example.curruaapp.api

import com.example.curruaapp.model.LoginRequest
import com.example.curruaapp.model.LoginResponse
import com.example.curruaapp.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interfaz que define los endpoints de la API para la autenticación.
 * Utiliza Retrofit para declarar las operaciones de red.
 */
interface AuthApi {

    /**
     * Realiza una petición de inicio de sesión a la API.
     * La anotación @POST indica que es una petición de tipo POST a la ruta "auth/login".
     * La función está marcada como 'suspend' para poder ser llamada desde una corrutina.
     *
     * @param body El cuerpo de la petición, que contiene el email y la contraseña del usuario.
     * @return Un objeto LoginResponse que contiene el token de autenticación si el login es exitoso.
     */
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    /**
     * Obtiene la información del usuario actual.
     * Necesita el token en la cabecera Authorization.
     */
    @GET("auth/me")
    suspend fun me(@Header("Authorization") token: String): UserResponse
}
