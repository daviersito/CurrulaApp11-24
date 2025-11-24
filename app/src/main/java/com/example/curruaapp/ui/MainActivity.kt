package com.example.curruaapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.curruaapp.api.AuthApi
import com.example.curruaapp.api.RetrofitClient
import com.example.curruaapp.api.TokenManager
import com.example.curruaapp.databinding.ActivityMainBinding
import com.example.curruaapp.model.LoginRequest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var tm: TokenManager
    private val authApi by lazy { RetrofitClient.authRetrofit().create(AuthApi::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        tm = TokenManager(this)

        // Si ya tenemos token, intentamos entrar directamente.
        if (!tm.getToken().isNullOrBlank()) {
            goHome()
            return
        }

        b.btnLogin.setOnClickListener {
            val email = b.etEmail.text.toString().trim()
            val pass = b.etPassword.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Ingresa email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            doLogin(email, pass)
        }
    }

    private fun doLogin(email: String, password: String) {
        b.progress.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // 1. Llamada al Login
                val resp = authApi.login(LoginRequest(email, password))
                
                val token = resp.authToken ?: resp.token
                if (token.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity, "Error: El servidor no devolvió un token", Toast.LENGTH_LONG).show()
                    return@launch
                }

                // 2. Obtener datos del usuario
                var user = resp.user

                // Fallback: Si por alguna razón el objeto user viene nulo, pedimos /auth/me
                if (user == null) {
                    try {
                        user = authApi.me("Bearer $token")
                    } catch (e: Exception) {
                        Log.e("LOGIN", "Error recuperando usuario en fallback", e)
                    }
                }

                // 3. Guardar datos
                tm.saveToken(token)
                
                val idToSave = user?.id ?: 0
                val nameToSave = user?.name ?: email.substringBefore("@")
                val emailToSave = user?.email ?: email
                
                // --- PARCHE DE SEGURIDAD: FORZAR ADMIN ---
                // Si es uno de tus correos, forzamos el rol de admin para evitar errores
                val roleToSave = if (emailToSave.equals("admin@duocuc.cl", ignoreCase = true) || 
                                     emailToSave.equals("1@duocuc.cl", ignoreCase = true)) {
                    "admin"
                } else {
                    // Para los demás, confiamos en el backend
                    if (!user?.role.isNullOrBlank()) user!!.role!! else "cliente"
                }

                // Guardamos todo en local
                tm.saveUser(idToSave, nameToSave, emailToSave, roleToSave)

                Log.d("LOGIN_SUCCESS", "Guardado -> ID: $idToSave, Nombre: $nameToSave, Rol: $roleToSave")
                Toast.makeText(this@MainActivity, "Bienvenido $nameToSave ($roleToSave)", Toast.LENGTH_SHORT).show()
                
                goHome()

            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Fallo en login", e)
                Toast.makeText(this@MainActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                b.progress.visibility = View.GONE
            }
        }
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
