package com.example.curruaapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.curruaapp.R
import com.example.curruaapp.api.TokenManager
import com.example.curruaapp.databinding.ActivityHomeBinding
import com.example.curruaapp.ui.fragments.*

class HomeActivity : AppCompatActivity() {

    private lateinit var b: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)

        val tm = TokenManager(this)
        val role = tm.getUserRole()

        // DEBUG: Ver en Logcat qué está pasando
        Log.d("HOME_DEBUG", "Rol recuperado en Home: '$role'")

        // SEGURIDAD: Si el rol está vacío (sesión antigua o error de carga),
        // cerramos sesión y mandamos al usuario al Login para recargar datos.
        if (role.isBlank()) {
            Log.w("HOME_DEBUG", "Rol vacío. Forzando logout para recuperar datos desde el servidor.")
            Toast.makeText(this, "Sesión incompleta. Por favor ingresa nuevamente.", Toast.LENGTH_LONG).show()
            
            tm.clear() // Borramos datos corruptos
            
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Si llegamos aquí, tenemos un rol válido
        val isAdmin = role.contains("admin", ignoreCase = true)
        
        // Mensaje informativo (opcional, para debug)
        // Toast.makeText(this, "Rol: $role", Toast.LENGTH_SHORT).show()

        setupNavigation(isAdmin)

        if (savedInstanceState == null) {
            // Cargamos el fragmento inicial
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, ProfileFragment())
            }
        }

        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    supportFragmentManager.commit { replace(R.id.fragmentContainer, ProfileFragment()) }
                    true
                }
                R.id.nav_products -> {
                    supportFragmentManager.commit { replace(R.id.fragmentContainer, ProductsFragment()) }
                    true
                }
                R.id.nav_cart -> {
                    supportFragmentManager.commit { replace(R.id.fragmentContainer, CartFragment()) }
                    true
                }
                R.id.nav_add -> {
                    supportFragmentManager.commit { replace(R.id.fragmentContainer, AddProductFragment()) }
                    true
                }
                R.id.nav_users -> {
                    supportFragmentManager.commit { replace(R.id.fragmentContainer, AdminUsersFragment()) }
                    true
                }
                R.id.nav_orders -> {
                    supportFragmentManager.commit { replace(R.id.fragmentContainer, AdminOrdersFragment()) }
                    true
                }
                else -> false
            }
        }
    }

    private fun setupNavigation(isAdmin: Boolean) {
        val menu = b.bottomNav.menu
        
        // Lógica de visibilidad:
        // El Cliente ve su Carrito, pero no las herramientas de Admin.
        menu.findItem(R.id.nav_cart).isVisible = !isAdmin
        
        // El Admin ve las herramientas de gestión, pero no el Carrito de compra.
        menu.findItem(R.id.nav_add).isVisible = isAdmin
        menu.findItem(R.id.nav_users).isVisible = isAdmin
        menu.findItem(R.id.nav_orders).isVisible = isAdmin
    }
}
