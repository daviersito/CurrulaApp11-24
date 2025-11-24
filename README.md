enlace video de youtube:   https://www.youtube.com/watch?v=S0-1aJoKtrg

# CurruñaApp 🍦

Aplicación Android de comercio electrónico para la venta de helados y productos artesanales. Incluye gestión de usuarios, carrito de compras, checkout y un panel de administración para gestionar productos y pedidos.

## 🛠️ Tech Stack

*   **Lenguaje:** Kotlin
*   **Arquitectura:** MVVM (Model-View-ViewModel)
*   **Red:** Retrofit 2 + OkHttp 3
*   **Serialización:** Gson
*   **Imágenes:** Glide
*   **Backend:** Xano (No-Code Database & API)
*   **Diseño:** XML Layouts, ViewBinding, Material Design

---

## ⚙️ Configuración del Backend (Xano)

Este proyecto utiliza **Xano** como backend. La API gestiona la base de datos de usuarios, productos y pedidos.

### Endpoints Clave
La API debe exponer los siguientes grupos de endpoints:
*   **Auth:** `/auth/login`, `/auth/signup`, `/auth/me`
*   **Productos:** `/product` (GET, POST, PATCH, DELETE)
*   **Pedidos:** `/order` (GET, POST)
*   **Imágenes:** `/upload/image` (POST - Multipart)
*   **Usuarios:** `/user/{id}` (GET, PATCH para perfil)

### Base de Datos
Estructura mínima requerida en Xano:
1.  **user:** `name`, `email`, `password`, `role` ('admin' | 'user'), `shipping_address`, `phone`.
2.  **product:** `name`, `description`, `price` (int), `stock` (int), `image` (list of images).
3.  **order:** `user_id` (relación), `total`, `status`, `address`, `items` (JSON o relación).

---

## 📱 Configuración Android

1.  **Clonar el proyecto:**
    Descarga el código fuente y ábrelo en **Android Studio**.
2.  **Sincronizar Gradle:**
    Asegúrate de tener conexión a internet para descargar las dependencias (Retrofit, Glide, etc.).
3.  **Verificar URL Base:**
    Abre `com.example.curruaapp.api.Constants.kt` (o `RetrofitClient.kt`) y confirma que la URL apunte a tu instancia de Xano.

---

## 🔗 Variables y URLs Necesarias

La aplicación requiere la siguiente URL base para conectar con la API.

**Base URL Actual:**
```kotlin
// Ubicación: com/example/curruaapp/api/RetrofitClient.kt
private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:ng05CpE3/"
```

> **Nota:** Si regeneras la API en Xano, debes actualizar esta constante en el código Android.

---

## 👥 Usuarios de Prueba (Demo)

Utiliza estas credenciales para probar los diferentes roles de la aplicación:

### 👑 Administrador
Tiene acceso al panel de "Admin" para crear productos y ver todos los pedidos.
*   **Email:** `admin@curruna.com`
*   **Contraseña:** `admin123`
*   **Rol:** `admin`

### 👤 Cliente
Acceso a la tienda, carrito, perfil y sus propios pedidos.
*   **Email:** `cliente@demo.com`
*   **Contraseña:** `cliente123`
*   **Rol:** `user`

*(Asegúrate de crear estos usuarios en tu base de datos de Xano si no existen).*

---

## 🖼️ Almacenamiento de Imágenes

Las imágenes de los productos se almacenan directamente en el **Xano Vault** (almacenamiento nativo de Xano).

*   **Subida:** La App sube las imágenes una por una al endpoint `/upload/image`.
*   **Asociación:** Xano devuelve un objeto `ImageResource` completo. La App envía una lista de estos objetos al crear/editar el producto.
*   **Visualización:** Android utiliza la librería **Glide** para cargar las URLs remotas que devuelve Xano.

> **Importante:** El campo en la base de datos de Xano se llama `image` (singular), pero contiene una lista (array) de objetos. La App maneja esto automáticamente mediante `ProductModels.kt`.
