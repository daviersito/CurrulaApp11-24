package com.example.curruaapp.api

import android.content.Context
import android.content.SharedPreferences

/**
 * Clase para gestionar el almacenamiento y la recuperación del token de autenticación
 * y la información del usuario usando SharedPreferences.
 *
 * @param context El contexto de la aplicación, necesario para acceder a SharedPreferences.
 */
class TokenManager(context: Context) {
    // Instancia de SharedPreferences para el almacenamiento de datos clave-valor.
    // Se inicializa con el nombre de preferencias y el modo privado.
    private val sp: SharedPreferences = context.getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)

    /**
     * Guarda el token de autenticación en SharedPreferences.
     * @param token El token a guardar.
     */
    fun saveToken(token: String) {
        sp.edit().putString(Constants.KEY_TOKEN, token).apply()
    }

    /**
     * Recupera el token de autenticación desde SharedPreferences.
     * @return El token guardado, o null si no se encuentra ninguno.
     */
    fun getToken(): String? = sp.getString(Constants.KEY_TOKEN, null)

    /**
     * Guarda la información del usuario en SharedPreferences.
     * @param id El ID del usuario.
     * @param name El nombre del usuario.
     * @param email El email del usuario.
     * @param role El rol del usuario (admin/client).
     */
    fun saveUser(id: Int?, name: String?, email: String?, role: String?) {
        sp.edit()
            .putInt(Constants.KEY_USER_ID, id ?: 0)
            .putString(Constants.KEY_USER_NAME, name ?: "")
            .putString(Constants.KEY_USER_EMAIL, email ?: "")
            .putString(Constants.KEY_USER_ROLE, role ?: "")
            .apply()
    }

    /**
     * Recupera el ID del usuario.
     */
    fun getUserId(): Int = sp.getInt(Constants.KEY_USER_ID, 0)

    /**
     * Recupera el nombre del usuario desde SharedPreferences.
     * @return El nombre del usuario guardado, o una cadena vacía si no se encuentra.
     */
    fun getUserName(): String = sp.getString(Constants.KEY_USER_NAME, "") ?: ""

    /**
     * Recupera el email del usuario desde SharedPreferences.
     * @return El email del usuario guardado, o una cadena vacía si no se encuentra.
     */
    fun getUserEmail(): String = sp.getString(Constants.KEY_USER_EMAIL, "") ?: ""

    /**
     * Recupera el rol del usuario desde SharedPreferences.
     * @return El rol del usuario guardado, o una cadena vacía si no se encuentra.
     */
    fun getUserRole(): String = sp.getString(Constants.KEY_USER_ROLE, "") ?: ""

    /**
     * Borra todos los datos guardados en SharedPreferences (token e información del usuario).
     * Útil para la funcionalidad de "cerrar sesión".
     */
    fun clear() {
        sp.edit().clear().apply()
    }
}
