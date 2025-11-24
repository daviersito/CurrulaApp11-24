package com.example.curruaapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.curruaapp.R

/**
 * Adaptador para un RecyclerView o ViewPager2 que muestra una lista de imágenes en un carrusel.
 * Este adaptador está diseñado para trabajar con una lista de IDs de recursos drawable.
 *
 * @param images La lista de IDs de los recursos de imagen (ej. R.drawable.mi_imagen) a mostrar.
 */
class CarouselAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    /**
     * ViewHolder para cada imagen en el carrusel.
     * Contiene una referencia directa al ImageView del layout del item.
     *
     * @param itemView La vista del layout para un único item del carrusel (item_image.xml).
     */
    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    /**
     * Se llama cuando RecyclerView necesita un nuevo [CarouselViewHolder].
     * Infla el layout del item de imagen desde XML y crea el ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return CarouselViewHolder(view)
    }

    /**
     * Se llama por RecyclerView para mostrar la imagen en la posición especificada.
     * Establece el recurso de imagen del [ImageView] en el [holder] según la posición.
     */
    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    /**
     * Devuelve el número total de imágenes en el carrusel.
     */
    override fun getItemCount(): Int = images.size
}