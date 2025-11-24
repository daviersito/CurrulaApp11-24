package com.example.curruaapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.curruaapp.databinding.ItemCartBinding
import com.example.curruaapp.model.CartItem
import com.example.curruaapp.utils.CartManager

class CartAdapter(
    private var items: List<CartItem>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    inner class VH(val b: ItemCartBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvName.text = item.product.name
        
        // Muestra el Subtotal (Precio x Cantidad)
        val subTotal = item.product.price * item.quantity
        holder.b.tvPrice.text = "$$subTotal"
        
        holder.b.tvQty.text = item.quantity.toString()

        holder.b.btnPlus.setOnClickListener {
            CartManager.add(item.product)
            notifyItemChanged(position)
            onUpdate()
        }

        holder.b.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                CartManager.updateQuantity(item.product, item.quantity - 1)
                notifyItemChanged(position)
            } else {
                CartManager.remove(item.product)
                items = CartManager.getItems()
                notifyDataSetChanged()
            }
            onUpdate()
        }
        
        holder.b.btnDelete.setOnClickListener {
            CartManager.remove(item.product)
            items = CartManager.getItems()
            notifyDataSetChanged()
            onUpdate()
        }
    }

    override fun getItemCount(): Int = items.size
}
