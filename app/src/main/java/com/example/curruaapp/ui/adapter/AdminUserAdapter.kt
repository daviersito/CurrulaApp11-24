package com.example.curruaapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.curruaapp.databinding.ItemUserBinding
import com.example.curruaapp.model.AdminUser

class AdminUserAdapter(
    private var users: List<AdminUser>,
    private val onBlockClick: (AdminUser, Boolean) -> Unit, // (User, shouldBlock)
    private val onUserClick: (AdminUser) -> Unit // (User) -> Click para editar
) : RecyclerView.Adapter<AdminUserAdapter.VH>() {

    inner class VH(val b: ItemUserBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = users[position]
        holder.b.tvName.text = user.name
        holder.b.tvEmail.text = user.email
        holder.b.tvRole.text = user.role

        // Configurar estado del botón Bloquear
        if (user.blocked) {
            holder.b.btnBlock.text = "Desbloquear"
        } else {
            holder.b.btnBlock.text = "Bloquear"
        }

        holder.b.btnBlock.setOnClickListener {
            // Invertir el estado actual
            onBlockClick(user, !user.blocked)
        }

        // Click en todo el item para editar
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int = users.size

    fun submit(list: List<AdminUser>) {
        users = list
        notifyDataSetChanged()
    }
}
