package com.example.curruaapp.api

import com.example.curruaapp.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface StoreApi {

    // --- PRODUCTOS ---

    @GET("product")
    suspend fun listProducts(
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("q") q: String? = null
    ): List<Product>

    @POST("product")
    suspend fun createProduct(@Body body: CreateProductRequest): Product

    @PATCH("product/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body body: CreateProductRequest
    ): Product

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Any

    @PATCH("product/{id}") 
    suspend fun patchProductImages(
        @Path("id") productId: Int,
        @Body request: PatchImagesRequest
    ): Product

    // CAMBIO: Subida de una sola imagen a la vez para mayor estabilidad
    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(
        @Part part: MultipartBody.Part
    ): ImageResource

    // --- CARRITO Y ORDEN ---

    @POST("cart")
    suspend fun createCart(@Body body: CreateCartRequest): CreateCartResponse

    @POST("cart_item")
    suspend fun addCartItem(@Body body: AddCartItemRequest): Any

    @POST("order")
    suspend fun createOrder(@Body body: CreateOrderRequest): Any

    // --- CLIENTE ---

    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Int): UserResponse

    @PATCH("user/{id}")
    suspend fun updateProfile(
        @Path("id") id: Int,
        @Body body: UpdateProfileRequest
    ): UserResponse

    // --- ADMIN: USUARIOS ---

    @GET("user")
    suspend fun listUsers(): List<AdminUser>

    @POST("user")
    suspend fun createUser(@Body body: CreateUserRequest): AdminUser
    
    @PATCH("user/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: UpdateUserRequest
    ): AdminUser

    @PATCH("user/{id}")
    suspend fun blockUser(
        @Path("id") id: Int,
        @Body body: BlockUserRequest
    ): AdminUser

    // --- ADMIN: ÓRDENES ---

    @GET("order")
    suspend fun listOrders(): List<Order>

    @PATCH("order/{id}")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Body body: UpdateOrderStatusRequest
    ): Order
}
