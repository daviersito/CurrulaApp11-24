package com.example.curruaapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.curruaapp.R
import com.example.curruaapp.api.RetrofitClient
import com.example.curruaapp.api.StoreApi
import com.example.curruaapp.databinding.FragmentProductsBinding
import com.example.curruaapp.model.Product
import com.example.curruaapp.ui.adapter.CarouselAdapter
import com.example.curruaapp.ui.adapter.ProductAdapter
import com.example.curruaapp.utils.CartManager
import kotlinx.coroutines.launch

/**
 * Fragmento para mostrar una lista de productos, un carrusel de imágenes y una barra de búsqueda.
 */
class ProductsFragment : Fragment() {
    // Binding para el layout, manejando su ciclo de vida.
    private var _b: FragmentProductsBinding? = null
    private val b get() = _b!!

    // Adaptador para la lista de productos (RecyclerView).
    private lateinit var adapter: ProductAdapter
    // Lista "maestra" que contiene todos los productos sin filtrar.
    private var master: List<Product> = emptyList()

    // Instancia de la API de la tienda, inicializada de forma perezosa.
    private val api by lazy { RetrofitClient.storeRetrofit(requireContext()).create(StoreApi::class.java) }

    /**
     * Infla el layout del fragmento.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentProductsBinding.inflate(inflater, container, false)
        return b.root
    }

    /**
     * Configura las vistas, adaptadores, listeners y carga los datos iniciales.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Configuración del RecyclerView de Productos ---
        adapter = ProductAdapter(emptyList()) { product ->
            CartManager.add(product)
            Toast.makeText(context, "${product.name} añadido al carrito", Toast.LENGTH_SHORT).show()
        }
        // Usa un GridLayoutManager para mostrar los productos en una cuadrícula de 2 columnas.
        b.tvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        b.tvProducts.adapter = adapter

        // --- Carga de Productos desde la API ---
        lifecycleScope.launch {
            try {
                // Llama a la API para obtener la lista de productos.
                master = api.listProducts(limit = 100, offset = 0, q = null)
                // Envía la lista completa al adaptador para que la muestre.
                adapter.submit(master)
            } catch (e: Exception) {
                // TODO: Mostrar un mensaje de error más informativo al usuario.
            }
        }

        // --- Configuración del Carrusel de Imágenes (ViewPager2) ---
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val btnNext: ImageButton = view.findViewById(R.id.btnNext)
        val btnPrev: ImageButton = view.findViewById(R.id.btnPrev)

        // Lista de imágenes locales para mostrar en el carrusel.
        val images = listOf(R.drawable.m1, R.drawable.m2, R.drawable.m4)
        val carouselAdapter = CarouselAdapter(images)
        viewPager.adapter = carouselAdapter

        // Configura los listeners para los botones de navegación del carrusel.
        btnNext.setOnClickListener {
            if (viewPager.currentItem < images.size - 1) {
                viewPager.currentItem += 1
            }
        }
        btnPrev.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem -= 1
            }
        }

        // --- Configuración de la Barra de Búsqueda ---
        b.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            // Se llama cuando el usuario envía la búsqueda.
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query ?: "")
                return true
            }
            // Se llama cada vez que el texto de la búsqueda cambia.
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText ?: "")
                return true
            }
        })
    }

    /**
     * Filtra la lista de productos 'master' basándose en una consulta de texto
     * y actualiza el adaptador con los resultados.
     * @param q La cadena de texto para filtrar.
     */
    private fun filterList(q: String) {
        val s = q.trim().lowercase()
        // Si la búsqueda está vacía, muestra la lista completa.
        if (s.isEmpty()) {
            adapter.submit(master)
            return
        }
        // Filtra la lista maestra buscando la cadena 's' en varios campos del producto.
        val filtered = master.filter { p ->
            listOf(p.name, p.brand, p.category, p.description).any {
                (it ?: "").lowercase().contains(s)
            }
        }
        // Envía la lista filtrada al adaptador.
        adapter.submit(filtered)
    }

    /**
     * Limpia la referencia al binding cuando la vista del fragmento se destruye.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}