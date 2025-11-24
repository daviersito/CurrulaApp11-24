package com.example.curruaapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para la petición de inicio de sesión.
 */
data class LoginRequest(val email: String, val password: String)

/**
 * Modelo de datos para la respuesta del endpoint de inicio de sesión.
 */
data class LoginResponse(
    val authToken: String? = null,
    val token: String? = null,
    val user: UserResponse? = null
)

/**
 * Modelo de datos para la información del usuario.
 * Actualizado para coincidir con la tabla de Xano.
 */
data class UserResponse(
    val id: Int,
    val created_at: Long?,
    val name: String?,
    val lastname: String?,
    val email: String?,
    val role: String?,
    val status: String?,
    @SerializedName("shipping_address")
    val shippingAddress: String?,
    val phone: String?
)

/**
 * Estructura para actualizar el perfil del usuario.
 */
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    @SerializedName("shipping_address")
    val address: String? = null,
    val phone: String? = null
)
