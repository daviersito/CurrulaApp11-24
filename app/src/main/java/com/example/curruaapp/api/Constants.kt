package com.example.curruaapp.api

/**
 * Objeto singleton para almacenar constantes utilizadas en toda la aplicación.
 * Esto ayuda a centralizar valores que se usan en múltiples lugares,
 * facilitando su mantenimiento y evitando valores "mágicos" hardcodeados en el código.
 */
object Constants {
    // URL base para la API de la tienda (Store).
    const val STORE_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:ng05CpE3/"
    // URL base para la API de autenticación (Auth).
    const val AUTH_BASE = "https://x8ki-letl-twmt.n7.xano.io/api:2pagbtNl/"

    // Nombre del archivo de SharedPreferences donde se guardan las preferencias de la app.
    const val PREFS = "xano_prefs"
    // Clave para guardar y recuperar el token de autenticación de SharedPreferences.
    const val KEY_TOKEN = "auth_token"
    // Clave para guardar y recuperar el ID del usuario.
    const val KEY_USER_ID = "user_id"
    // Clave para guardar y recuperar el nombre del usuario de SharedPreferences.
    const val KEY_USER_NAME = "user_name"
    // Clave para guardar y recuperar el email del usuario de SharedPreferences.
    const val KEY_USER_EMAIL = "user_email"
    // Clave para guardar y recuperar el rol del usuario de SharedPreferences.
    const val KEY_USER_ROLE = "user_role"
}
