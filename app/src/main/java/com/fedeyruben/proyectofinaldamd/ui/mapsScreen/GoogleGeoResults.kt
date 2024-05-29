package com.fedeyruben.proyectofinaldamd.ui.mapsScreen

data class GoogleGeoResults(
    val results: List<Results>
)

data class Results(
    val geometry: Geometry,
    val formatted_address: String
)
data class Geometry(
    val location: UserLocation
)
data class UserLocation(
    val lat: Double,
    val lng: Double
)