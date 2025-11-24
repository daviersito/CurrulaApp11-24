package com.example.curruaapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.curruaapp.api.RetrofitClient
import com.example.curruaapp.api.StoreApi
import com.example.curruaapp.api.TokenManager
import com.example.curruaapp.databinding.FragmentCartBinding
import com.example.curruaapp.model.*
import com.example.curruaapp.ui.adapter.CartAdapter
import com.example.curruaapp.utils.CartManager
import kotlinx.coroutines.launch

class CartFragment : Fragment() {
    private var _b: FragmentCartBinding? = null
    private val b get() = _b!!
    
    private val api by lazy { RetrofitClient.storeRetrofit(requireContext()).create(StoreApi::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentCartBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        updateTotal()
        
        // Cargar dirección del usuario automáticamente
        loadUserAddress()

        b.btnCheckout.setOnClickListener {
            val tm = TokenManager(requireContext())
            val userId = tm.getUserId()
            
            if (userId == 0) {
                Toast.makeText(context, "⚠️ ERROR DE SESIÓN: Tu ID es 0. Por favor Cierra Sesión y vuelve a entrar.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            placeOrder()
        }
    }

    private fun loadUserAddress() {
        val tm = TokenManager(requireContext())
        val userId = tm.getUserId()
        if (userId != 0) {
            lifecycleScope.launch {
                try {
                    val user = api.getUser(userId)
                    // Si el usuario tiene dirección guardada, la ponemos en el campo
                    if (!user.shippingAddress.isNullOrEmpty()) {
                        b.etAddress.setText(user.shippingAddress)
                    }
                } catch (e: Exception) {
                    // Si falla la carga del perfil, no hacemos nada (el usuario escribirá manual)
                }
            }
        }
    }

    private fun setupRecycler() {
        val adapter = CartAdapter(CartManager.getItems()) {
            updateTotal()
        }
        b.rvCart.layoutManager = LinearLayoutManager(requireContext())
        b.rvCart.adapter = adapter
    }

    private fun updateTotal() {
        val total = CartManager.getTotal()
        b.tvTotal.text = "Total: $$total"
        b.btnCheckout.isEnabled = total > 0
    }

    private fun placeOrder() {
        val tm = TokenManager(requireContext())
        val userId = tm.getUserId()
        val address = b.etAddress.text.toString().trim() // Leemos la dirección

        if (userId == 0) {
            Toast.makeText(context, "Error: No se pudo identificar al usuario.", Toast.LENGTH_LONG).show()
            return
        }
        
        if (address.isEmpty()) {
            Toast.makeText(context, "Por favor, ingresa una dirección de envío.", Toast.LENGTH_SHORT).show()
            b.etAddress.error = "Obligatorio"
            b.etAddress.requestFocus() // Poner foco para que el usuario vea dónde escribir
            return
        }

        if (CartManager.getItems().isEmpty()) {
            Toast.makeText(context, "El carrito está vacío.", Toast.LENGTH_SHORT).show()
            return
        }

        b.progress.visibility = View.VISIBLE
        b.btnCheckout.isEnabled = false

        lifecycleScope.launch {
            try {
                // 1. Crear el Carrito en el servidor
                val cartResp = api.createCart(CreateCartRequest(user_id = userId))
                val cartId = cartResp.id

                // 2. Enviar cada producto del carrito al servidor
                val items = CartManager.getItems()
                items.forEach { item ->
                    item.product.id?.let { productId ->
                        api.addCartItem(AddCartItemRequest(
                            cart_id = cartId,
                            product_id = productId,
                            quantity = item.quantity
                        ))
                    }
                }

                // 3. Crear la Orden final (enviando la dirección)
                val total = CartManager.getTotal().toInt()
                api.createOrder(CreateOrderRequest(
                    user_id = userId,
                    total = total,
                    address = address,
                    status = "pending"
                ))

                // Éxito
                Toast.makeText(context, "¡Orden enviada correctamente!", Toast.LENGTH_LONG).show()
                
                // Limpiar carrito local y UI
                CartManager.clear()
                setupRecycler()
                updateTotal()
                b.etAddress.text.clear()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error al procesar compra: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                b.progress.visibility = View.GONE
                b.btnCheckout.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
