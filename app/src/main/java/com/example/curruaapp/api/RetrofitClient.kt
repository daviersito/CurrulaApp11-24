package com.example.curruaapp.api

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton para crear y configurar las instancias de Retrofit.
 * Proporciona clientes de Retrofit listos para ser usados para la API de autenticación y la API de la tienda.
 */
object RetrofitClient {

    /**
     * Crea y configura una instancia de Retrofit para los endpoints de autenticación.
     * Esta instancia no requiere un token de autorización.
     *
     * @return Una instancia de Retrofit configurada para la URL base de autenticación.
     */
    fun authRetrofit(): Retrofit {
        // Crea un interceptor para registrar en la consola el cuerpo de las peticiones y respuestas HTTP.
        // Es muy útil para depurar. El nivel se establece en BODY para ver la información completa.
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        // Crea un cliente OkHttp y le añade el interceptor de logging.
        val client = OkHttpClient.Builder().addInterceptor(logger).build()

        // Construye y devuelve la instancia de Retrofit.
        return Retrofit.Builder()
            .baseUrl(Constants.AUTH_BASE) // Establece la URL base para la API de autenticación.
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())) // Añade un convertidor para manejar JSON.
            .client(client) // Asigna el cliente OkHttp configurado.
            .build()
    }

    /**
     * Crea y configura una instancia de Retrofit para los endpoints de la tienda (Store).
     * Esta instancia incluye un interceptor que añade automáticamente el token de autenticación
     * a todas las peticiones.
     *
     * @param context El contexto de la aplicación, necesario para inicializar el TokenManager.
     * @return Una instancia de Retrofit configurada para la URL base de la tienda.
     */
    fun storeRetrofit(context: Context): Retrofit {
        // Inicializa el TokenManager para poder acceder al token guardado.
        val tm = TokenManager(context)
        // Crea el interceptor de logging.
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        
        // Crea un cliente OkHttp y le añade dos interceptores:
        val client = OkHttpClient.Builder()
            // 1. El AuthInterceptor, que añade la cabecera "Authorization" a cada petición.
            //    Se le pasa una función lambda que obtiene el token actual desde el TokenManager.
            .addInterceptor(AuthInterceptor { tm.getToken() })
            // 2. El interceptor de logging.
            .addInterceptor(logger)
            .build()

        // Construye y devuelve la instancia de Retrofit.
        return Retrofit.Builder()
            .baseUrl(Constants.STORE_BASE) // Establece la URL base para la API de la tienda.
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(client)
            .build()
    }
}