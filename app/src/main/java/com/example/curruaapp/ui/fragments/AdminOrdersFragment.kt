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
import com.example.curruaapp.databinding.FragmentAdminOrdersBinding
import com.example.curruaapp.model.Order
import com.example.curruaapp.model.UpdateOrderStatusRequest
import com.example.curruaapp.ui.adapter.AdminOrderAdapter
import kotlinx.coroutines.launch

class AdminOrdersFragment : Fragment() {
    private var _b: FragmentAdminOrdersBinding? = null
    private val b get() = _b!!

    private val api by lazy { RetrofitClient.storeRetrofit(requireContext()).create(StoreApi::class.java) }
    private lateinit var adapter: AdminOrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentAdminOrdersBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AdminOrderAdapter(emptyList()) { order, newStatus ->
            updateOrderStatus(order, newStatus)
        }

        b.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        b.rvOrders.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        b.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val orders = api.listOrders()
                adapter.submit(orders)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar pedidos: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                b.progress.visibility = View.GONE
            }
        }
    }

    private fun updateOrderStatus(order: Order, status: String) {
        lifecycleScope.launch {
            try {
                api.updateOrderStatus(order.id, UpdateOrderStatusRequest(status))
                Toast.makeText(context, "Pedido actualizado a $status", Toast.LENGTH_SHORT).show()
                loadOrders() // Recargar lista
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
