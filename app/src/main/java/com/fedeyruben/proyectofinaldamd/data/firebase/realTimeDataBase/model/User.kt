package com.fedeyruben.proyectofinaldamd.data.firebase.realTimeDataBase.model

import com.google.android.gms.maps.model.LatLng


/** Almacenar todos los datos que vamos a necesitar*/
data class User(
    // La key será necesaria para guardar más tarde lo que recibia de la BD
    var key: String? = null,
    var userId: String,
    var name: String,
    var location: LatLng
)
