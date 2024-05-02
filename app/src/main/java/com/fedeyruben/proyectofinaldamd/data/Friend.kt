package com.fedeyruben.proyectofinaldamd.data.dataStore.model

import android.net.Uri
data class Friend(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val imageResId: Uri? = null // Opcional: ID del recurso de imagen personalizado
)