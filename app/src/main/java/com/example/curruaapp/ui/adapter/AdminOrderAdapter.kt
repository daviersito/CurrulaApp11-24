package com.example.curruaapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.curruaapp.databinding.ItemOrderBinding
import com.example.curruaapp.model.Order

class AdminOrderAdapter(
    private var orders: List<Order>,
    private val onStatusChange: (Order, String) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.VH>() {

    inner class VH(val b: ItemOrderBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val order = orders[position]
        
        holder.b.tvOrderId.text = "Pedido #${order.id}"
        
        // Lógica final limpia: Si hay usuario se muestra, si no se oculta.
        if (order.user != null) {
            val userName = order.user.name ?: "Usuario"
            val userEmail = order.user.email ?: ""
            holder.b.tvUser.text = "$userName ($userEmail)"
            holder.b.tvUser.visibility = View.VISIBLE
        } else {
            holder.b.tvUser.visibility = View.GONE
        }
        
        holder.b.tvTotal.text = "$${order.total_price}"
        
        holder.b.tvStatus.text = order.status.uppercase()
        
        // SOLICITUD USUARIO: Ocultar completamente la dirección en el gestor
        holder.b.tvAddress.visibility = View.GONE

        // Control de botones
        val isPending = order.status == "pending"
        holder.b.btnApprove.isEnabled = isPending
        holder.b.btnReject.isEnabled = isPending
        holder.b.btnApprove.alpha = if (isPending) 1.0f else 0.5f
        holder.b.btnReject.alpha = if (isPending) 1.0f else 0.5f

        holder.b.btnApprove.setOnClickListener {
            onStatusChange(order, "sent")
        }

        holder.b.btnReject.setOnClickListener {
            onStatusChange(order, "rejected")
        }
    }

    override fun getItemCount(): Int = orders.size

    fun submit(list: List<Order>) {
        orders = list
        notifyDataSetChanged()
    }
}
