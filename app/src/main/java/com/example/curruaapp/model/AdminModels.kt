package com.example.curruaapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de usuario para la gestión de administradores.
 */
data class AdminUser(
    val id: Int,
    val name: String,
    val email: String,
    val role: String, // "admin", "user", "cliente"
    val blocked: Boolean = false
)

/**
 * Modelo para crear usuario desde admin.
 */
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

/**
 * Modelo para editar usuario desde admin.
 */
data class UpdateUserRequest(
    val name: String,
    val email: String,
    val role: String
)

/**
 * Clase auxiliar para mapear los datos del usuario que vienen dentro de la orden (Addon).
 */
data class OrderUser(
    val name: String?,
    val email: String?,
    @SerializedName("shipping_address")
    val shippingAddress: String? = null
)

/**
 * Modelo de Orden recibido del backend.
 * Actualizado para leer "total" y el objeto "_user" de Xano.
 */
data class Order(
    val id: Int,
    val user_id: Int, // Añadido para depuración
    
    @SerializedName("total")
    val total_price: Long, // Mapea el campo JSON "total"
    
    // TRUCO: Usamos "alternate" para que funcione si el addon se llama "_user", "user" o "users"
    @SerializedName(value = "_user", alternate = ["user", "users"])
    val user: OrderUser? = null, 
    
    val items_json: String? = null,
    val address: String? = null,
    val status: String, 
    val created_at: Long? = null
)

/**
 * Petición para cambiar el estado de una orden.
 */
data class UpdateOrderStatusRequest(
    val status: String
)

/**
 * Petición para bloquear/desbloquear usuario.
 */
data class BlockUserRequest(
    val blocked: Boolean
)
