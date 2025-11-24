package com.example.curruaapp.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.curruaapp.api.RetrofitClient
import com.example.curruaapp.api.StoreApi
import com.example.curruaapp.databinding.FragmentAddProductBinding
import com.example.curruaapp.model.CreateProductRequest
import com.example.curruaapp.model.ImageResource
import com.example.curruaapp.model.PatchImagesRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AddProductFragment : Fragment() {
    private var _b: FragmentAddProductBinding? = null
    private val b get() = _b!!

    private val api by lazy { RetrofitClient.storeRetrofit(requireContext()).create(StoreApi::class.java) }
    private var pickedUris: List<Uri> = emptyList()

    private val pickImages = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        pickedUris = uris ?: emptyList()
        b.tvPicked.text = "${pickedUris.size} imágenes seleccionadas"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentAddProductBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.btnPick.setOnClickListener { pickImages.launch("image/*") }
        b.btnCreate.setOnClickListener { submit() }
    }

    private fun submit() {
        val name = b.etName.text.toString().trim()
        val brand = b.etBrand.text.toString().trim()
        val category = b.etCategory.text.toString().trim()
        val price = b.etPrice.text.toString().trim().toLongOrNull() ?: 0L
        val stock = b.etStock.text.toString().trim().toIntOrNull() ?: 0
        val desc = b.etDesc.text.toString().trim()

        if (name.isEmpty() || price <= 0 || stock < 0) {
            toast("Completa nombre, precio (>0) y stock (>=0)")
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            try {
                // 1. Crear el producto
                Log.d("API_TEST", "1. Creando producto...")
                val created = api.createProduct(
                    CreateProductRequest(name, desc, price, stock, brand, category)
                )
                Log.d("API_TEST", "-> Producto creado ID: ${created.id}")
                
                if (created.id == null) {
                     throw Exception("El producto se creó pero no devolvió ID.")
                }

                // 2. Subir imágenes una por una (MÉTODO SEGURO)
                val uploadedImages = mutableListOf<ImageResource>()
                
                if (pickedUris.isNotEmpty()) {
                    Log.d("API_TEST", "2. Subiendo ${pickedUris.size} imágenes...")
                    
                    withContext(Dispatchers.IO) {
                        val cr = requireContext().contentResolver
                        pickedUris.forEachIndexed { index, uri ->
                            try {
                                val fileName = "img_${System.currentTimeMillis()}_$index.jpg"
                                cr.openInputStream(uri)?.use { input ->
                                    val bytes = input.readBytes()
                                    val req = RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes)
                                    // Enviamos "content" sin corchetes, uno a uno
                                    val part = MultipartBody.Part.createFormData("content", fileName, req)
                                    
                                    val result = api.uploadImage(part)
                                    uploadedImages.add(result)
                                    Log.d("API_TEST", "-> Imagen $index subida OK: ${result.name}")
                                }
                            } catch (e: Exception) {
                                Log.e("API_ERROR", "Error subiendo imagen $index", e)
                            }
                        }
                    }
                    Log.d("API_TEST", "-> Total imágenes subidas: ${uploadedImages.size}")
                }

                // 3. Actualizar producto con la lista de imágenes completa
                if (uploadedImages.isNotEmpty()) {
                    Log.d("API_TEST", "3. Asociando imágenes al producto ${created.id}...")
                    val patchRequest = PatchImagesRequest(uploadedImages)
                    api.patchProductImages(created.id, patchRequest)
                    Log.d("API_TEST", "-> Patch finalizado")
                }

                toast("¡Producto creado con éxito!")
                clearForm()

            } catch (e: HttpException) {
                val errorUrl = e.response()?.raw()?.request?.url.toString()
                val code = e.code()
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("API_ERROR", "Error $code en URL: $errorUrl. Body: $errorBody")
                toast("Error $code al llamar a: ...${errorUrl.takeLast(20)}")
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al crear producto", e)
                toast("Error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        b.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
        b.btnCreate.isEnabled = !isLoading
        b.btnPick.isEnabled = !isLoading
    }

    private fun clearForm() {
        b.etName.text?.clear()
        b.etBrand.text?.clear()
        b.etCategory.text?.clear()
        b.etPrice.text?.clear()
        b.etStock.text?.clear()
        b.etDesc.text?.clear()
        pickedUris = emptyList()
        b.tvPicked.text = "0 imágenes seleccionadas"
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
