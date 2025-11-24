package com.example.curruaapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.curruaapp.R
import com.example.curruaapp.databinding.ItemProductBinding
import com.example.curruaapp.model.Product

/**
 * Adaptador para el RecyclerView que muestra una lista de productos.
 * Se encarga de tomar la lista de datos [Product] y vincularla a las vistas
 * individuales de cada elemento de la lista.
 *
 * @param data La lista inicial de productos a mostrar.
 * @param onProductClick Lambda que se ejecuta cuando se hace clic en un producto (para añadir al carrito).
 */
class ProductAdapter(
    private var data: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    /**
     * ViewHolder para cada elemento de la lista de productos.
     * Contiene una referencia al binding del layout del item, lo que permite
     * un acceso eficiente a las vistas (TextViews, ImageView, etc.).
     *
     * @param b El objeto de binding para el layout item_product.xml.
     */
    inner class VH(val b: ItemProductBinding) : RecyclerView.ViewHolder(b.root)

    /**
     * Se llama cuando RecyclerView necesita un nuevo [VH] del tipo dado para representar un elemento.
     * Infla el layout del item desde XML y crea el ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    /**
     * Se llama por RecyclerView para mostrar los datos en la posición especificada.
     * Vincula los datos del producto [p] a las vistas dentro del [holder].
     */
    override fun onBindViewHolder(holder: VH, position: Int) {
        // Obtiene el producto en la posición actual.
        val p = data[position]

        // Asigna los datos del producto a las vistas correspondientes.
        holder.b.tvName.text = p.name
        holder.b.tvBrandCategory.text = listOfNotNull(p.brand, p.category).joinToString(" • ")
        holder.b.tvPrice.text = "$${p.price}"

        // Carga la imagen del producto.
        // Si la lista de imágenes está vacía o es nula, usa una imagen local de fallback.
        if (p.images.isNullOrEmpty()) {
            holder.b.imgCover.setImageResource(R.drawable.helao) // 'helao' parece un placeholder
        } else {
            // Si hay imágenes, toma la URL de la primera y la carga usando Glide.
            val url = p.images.first().url
            Glide.with(holder.itemView)
                .load(url)
                .into(holder.b.imgCover)
        }

        // Configura el click listener en todo el item para añadir al carrito
        holder.itemView.setOnClickListener {
            onProductClick(p)
        }
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
     */
    override fun getItemCount(): Int = data.size

    /**
     * Actualiza la lista de productos del adaptador y notifica al RecyclerView
     * para que se vuelva a dibujar.
     *
     * @param list La nueva lista de productos.
     */
    fun submit(list: List<Product>) {
        data = list
        notifyDataSetChanged() // Notifica que los datos han cambiado.
    }
}