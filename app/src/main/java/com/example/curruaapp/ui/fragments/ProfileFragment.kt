package com.example.curruaapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.curruaapp.api.RetrofitClient
import com.example.curruaapp.api.StoreApi
import com.example.curruaapp.api.TokenManager
import com.example.curruaapp.databinding.FragmentProfileBinding
import com.example.curruaapp.model.UpdateProfileRequest
import com.example.curruaapp.ui.MainActivity
import kotlinx.coroutines.launch

/**
 * Fragmento para mostrar el perfil del usuario, permitir editarlo y cerrar sesión.
 */
class ProfileFragment : Fragment() {
    private var _b: FragmentProfileBinding? = null
    private val b get() = _b!!
    
    // Instancia de la API (usamos StoreApi para actualizar perfil)
    private val api by lazy { RetrofitClient.storeRetrofit(requireContext()).create(StoreApi::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentProfileBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tm = TokenManager(requireContext())

        // 1. Cargar datos básicos de SharedPreferences mientras esperamos a la red
        b.etName.setText(tm.getUserName())
        b.etEmail.setText(tm.getUserEmail())

        // 2. Cargar datos completos del servidor (incluyendo dirección y teléfono)
        loadUserProfile(tm)

        b.btnUpdate.setOnClickListener {
            val name = b.etName.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val address = b.etAddress.text.toString().trim()
            val phone = b.etPhone.text.toString().trim()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(context, "Nombre y Email son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateProfile(name, email, address, phone, tm)
        }

        b.btnLogout.setOnClickListener {
            tm.clear()
            logout()
        }
    }

    private fun loadUserProfile(tm: TokenManager) {
        // Mostrar carga visual si lo deseas, o dejarlo silencioso
        // b.progress.visibility = View.VISIBLE 
        
        lifecycleScope.launch {
            try {
                // Obtenemos el perfil completo usando el ID guardado
                val user = api.getUser(tm.getUserId())
                
                // Rellenamos los campos con lo que viene del servidor
                b.etName.setText(user.name)
                b.etEmail.setText(user.email)
                
                // Si el servidor devuelve null en dirección o teléfono, ponemos texto vacío
                b.etAddress.setText(user.shippingAddress ?: "")
                b.etPhone.setText(user.phone ?: "")

                // Opcional: Actualizar SharedPreferences para tener el nombre más reciente
                tm.saveUser(user.id, user.name ?: "", user.email ?: "", user.role ?: "user")

            } catch (e: Exception) {
                // Si falla la carga, no pasa nada grave, el usuario ve los datos locales
                // Solo mostramos error si es crítico, aquí un log o Toast discreto
                // Toast.makeText(context, "No se pudo cargar perfil completo", Toast.LENGTH_SHORT).show()
            } finally {
                // b.progress.visibility = View.GONE
            }
        }
    }

    private fun updateProfile(name: String, email: String, address: String, phone: String, tm: TokenManager) {
        b.progress.visibility = View.VISIBLE
        b.btnUpdate.isEnabled = false

        lifecycleScope.launch {
            try {
                val req = UpdateProfileRequest(name, email, address, phone)
                // Llamada a la API para actualizar
                val updatedUser = api.updateProfile(tm.getUserId(), req)
                
                // Actualizar datos locales
                tm.saveUser(updatedUser.id, updatedUser.name ?: name, updatedUser.email ?: email, updatedUser.role ?: "user")
                
                // Actualizamos la UI con la respuesta confirmada del servidor
                b.etAddress.setText(updatedUser.shippingAddress ?: address)
                b.etPhone.setText(updatedUser.phone ?: phone)
                
                Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                b.progress.visibility = View.GONE
                b.btnUpdate.isEnabled = true
            }
        }
    }

    private fun logout() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
