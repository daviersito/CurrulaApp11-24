package com.example.curruaapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para representar un recurso de imagen de Xano.
 * Incluye todos los metadatos para no perder información al re-enviarla.
 */
data class ImageResource(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("key") val key: Int? = null,
    val url: String? = null,
    val path: String? = null,
    val mime: String? = null,
    val name: String? = null,
    val access: String? = null,
    val type: String? = null,
    val size: Int? = null,
    val meta: Map<String, Any>? = null
)
