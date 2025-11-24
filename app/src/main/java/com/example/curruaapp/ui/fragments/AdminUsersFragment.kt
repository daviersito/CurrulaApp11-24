package com.example.curruaapp.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.curruaapp.api.RetrofitClient
import com.example.curruaapp.api.StoreApi
import com.example.curruaapp.databinding.DialogCreateUserBinding
import com.example.curruaapp.databinding.FragmentAdminUsersBinding
import com.example.curruaapp.model.AdminUser
import com.example.curruaapp.model.BlockUserRequest
import com.example.curruaapp.model.CreateUserRequest
import com.example.curruaapp.model.UpdateUserRequest
import com.example.curruaapp.ui.adapter.AdminUserAdapter
import kotlinx.coroutines.launch

class AdminUsersFragment : Fragment() {
    private var _b: FragmentAdminUsersBinding? = null
    private val b get() = _b!!

    private val api by lazy { RetrofitClient.storeRetrofit(requireContext()).create(StoreApi::class.java) }
    private lateinit var adapter: AdminUserAdapter
    
    private var fullUserList: List<AdminUser> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = AdminUserAdapter(
            emptyList(),
            onBlockClick = { user, shouldBlock -> toggleBlockUser(user, shouldBlock) },
            onUserClick = { user -> showEditUserDialog(user) }
        )
        
        b.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        b.rvUsers.adapter = adapter

        b.fabAddUser.setOnClickListener {
            showCreateUserDialog()
        }
        
        b.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadUsers()
    }

    private fun loadUsers() {
        b.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                fullUserList = api.listUsers()
                filterList(b.etSearch.text.toString())
            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                b.progress.visibility = View.GONE
            }
        }
    }

    private fun filterList(query: String) {
        val filtered = if (query.isEmpty()) {
            fullUserList
        } else {
            fullUserList.filter { 
                (it.name?.contains(query, ignoreCase = true) == true) || 
                (it.email?.contains(query, ignoreCase = true) == true) 
            }
        }
        adapter.submit(filtered)
    }

    private fun showCreateUserDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogCreateUserBinding.inflate(layoutInflater)
        
        val roles = arrayOf("cliente", "admin")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        dialogBinding.spRole.adapter = spinnerAdapter

        builder.setView(dialogBinding.root)
            .setTitle("Crear Usuario")
            .setPositiveButton("Crear") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val email = dialogBinding.etEmail.text.toString().trim()
                val password = dialogBinding.etPassword.text.toString().trim()
                val role = dialogBinding.spRole.selectedItem.toString()

                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    createUser(name, email, password, role)
                } else {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun createUser(n: String, e: String, p: String, r: String) {
        b.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                api.createUser(CreateUserRequest(n, e, p, r))
                Toast.makeText(context, "Usuario creado", Toast.LENGTH_SHORT).show()
                loadUsers()
            } catch (ex: Exception) {
                Toast.makeText(context, "Error al crear: ${ex.message}", Toast.LENGTH_LONG).show()
            } finally {
                b.progress.visibility = View.GONE
            }
        }
    }

    private fun showEditUserDialog(user: AdminUser) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogBinding = DialogCreateUserBinding.inflate(layoutInflater)

        dialogBinding.etName.setText(user.name)
        dialogBinding.etEmail.setText(user.email)
        
        // Ocultamos campo password al editar
        dialogBinding.etPassword.visibility = View.GONE 
        dialogBinding.tilPassword.visibility = View.GONE

        val roles = arrayOf("cliente", "admin")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        dialogBinding.spRole.adapter = spinnerAdapter
        
        val roleIndex = roles.indexOf(user.role)
        if (roleIndex >= 0) dialogBinding.spRole.setSelection(roleIndex)

        builder.setView(dialogBinding.root)
            .setTitle("Editar Usuario #${user.id}")
            .setPositiveButton("Guardar") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val email = dialogBinding.etEmail.text.toString().trim()
                val role = dialogBinding.spRole.selectedItem.toString()

                if (name.isNotEmpty() && email.isNotEmpty()) {
                    updateUser(user.id, name, email, role)
                } else {
                    Toast.makeText(context, "Nombre y Email obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun updateUser(id: Int, n: String, e: String, r: String) {
        b.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                api.updateUser(id, UpdateUserRequest(n, e, r))
                Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                loadUsers()
            } catch (ex: Exception) {
                Toast.makeText(context, "Error al actualizar: ${ex.message}", Toast.LENGTH_LONG).show()
            } finally {
                b.progress.visibility = View.GONE
            }
        }
    }

    private fun toggleBlockUser(user: AdminUser, shouldBlock: Boolean) {
        lifecycleScope.launch {
            try {
                api.blockUser(user.id, BlockUserRequest(shouldBlock))
                Toast.makeText(context, "Usuario ${if (shouldBlock) "bloqueado" else "desbloqueado"}", Toast.LENGTH_SHORT).show()
                loadUsers()
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
