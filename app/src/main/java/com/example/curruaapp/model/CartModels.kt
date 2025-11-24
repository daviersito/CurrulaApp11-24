package com.example.curruaapp.model

/**
 * Representa un ítem dentro del carrito de compras local.
 */
data class CartItem(
    val product: Product,
    var quantity: Int
)

/**
 * Petición para crear un carrito.
 */
data class CreateCartRequest(
    val user_id: Int
)

/**
 * Respuesta al crear un carrito (necesitamos el ID).
 */
data class CreateCartResponse(
    val id: Int
)

/**
 * Petición para agregar un ítem al carrito en el servidor.
 */
data class AddCartItemRequest(
    val cart_id: Int,
    val product_id: Int,
    val quantity: Int
)

/**
 * Estructura para enviar una orden al backend (Nueva estructura).
 */
data class CreateOrderRequest(
    val user_id: Int,
    val total: Int,
    val address: String, // Nuevo campo dirección
    val status: String = "pending"
)
