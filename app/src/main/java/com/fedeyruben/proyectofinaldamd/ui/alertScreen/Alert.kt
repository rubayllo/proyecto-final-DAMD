package com.fedeyruben.proyectofinaldamd.ui.alertScreen

import com.google.firebase.firestore.GeoPoint

data class Alert(
    val alertLevel: String? = null,
    val geoPoint: GeoPoint? = null,
    val isAlert: Boolean = false,
    val phoneNumber: String? = null
)
