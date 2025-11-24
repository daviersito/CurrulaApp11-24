package com.example.curruaapp.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para metadatos de una imagen, como sus dimensiones.
 *
 * @property width El ancho de la imagen en píxeles.
 * @property height La altura de la imagen en píxeles.
 */
data class Meta(val width: Int? = null, val height: Int? = null)

/**
 * Modelo de datos principal para representar un producto.
 * Contiene toda la información detallada de un producto de la tienda.
 *
 * @property id El identificador único del producto.
 * @property name El nombre del producto.
 * @property description Una descripción detallada del producto.
 * @property price El precio del producto. Se usa Long para manejar diferentes magnitudes monetarias.
 * @property stock La cantidad de unidades disponibles en inventario.
 * @property brand La marca del producto.
 * @property category La categoría a la que pertenece el producto.
 * @property images Una lista de recursos de imagen ([ImageResource]) asociados a este producto.
 */
data class Product(
    val id: Int? = null,
    val name: String,
    val description: String,
    val price: Long,
    val stock: Int,
    val brand: String,
    val category: String,
    @SerializedName("image") val images: List<ImageResource>? = null
)

/**
 * Modelo de datos para la petición de creación de un nuevo producto.
 * Contiene los campos necesarios para registrar un producto en el sistema,
 * excluyendo aquellos que genera el servidor (como el 'id' o las 'images' inicialmente).
 *
 * @property name El nombre del producto.
 * @property description Una descripción detallada del producto.
 * @property price El precio del producto.
 * @property stock La cantidad de unidades disponibles.
 * @property brand La marca del producto.
 * @property category La categoría a la que pertenece el producto.
 */
data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Long,
    val stock: Int,
    val brand: String,
    val category: String
)
