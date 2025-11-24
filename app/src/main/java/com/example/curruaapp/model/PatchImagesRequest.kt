package com.example.curruaapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo para asociar imágenes a un producto (PATCH).
 * Usamos @SerializedName("image") porque la columna en la BD se llama "image".
 */
data class PatchImagesRequest(
    @SerializedName("image")
    val images: List<ImageResource>
)
