package com.example.curruaapp.utils

import com.example.curruaapp.model.CartItem
import com.example.curruaapp.model.Product

object CartManager {
    private val items = mutableListOf<CartItem>()

    fun add(product: Product) {
        val existing = items.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            items.add(CartItem(product, 1))
        }
    }

    fun remove(product: Product) {
        items.removeAll { it.product.id == product.id }
    }

    fun updateQuantity(product: Product, quantity: Int) {
        if (quantity <= 0) {
            remove(product)
            return
        }
        val existing = items.find { it.product.id == product.id }
        existing?.quantity = quantity
    }

    fun getItems(): List<CartItem> = items.toList()

    fun getTotal(): Long {
        return items.sumOf { it.product.price * it.quantity }
    }

    fun clear() {
        items.clear()
    }
    
    fun itemCount(): Int = items.sumOf { it.quantity }
}