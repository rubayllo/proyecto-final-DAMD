package com.fedeyruben.proyectofinaldamd.ui.mapsScreen


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fedeyruben.proyectofinaldamd.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MapScreenInit() {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }

    /** todo recuperar ru de ALERTA */
    val endPoint = LatLng(36.58929, -4.5814)

    // Function to request location permission
    fun requestLocationPermission() {
        val REQUEST_LOCATION_PERMISSION_CODE = 1001
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION_CODE)
        }
    }

    // Obtener la ubicación actual y la ruta
    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    userLocation = currentLatLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 12f)

                    // Función para obtener direcciones y actualizar la ruta
                    CoroutineScope(Dispatchers.IO).launch {
                        val directionsResult = getDirections(currentLatLng, endPoint, context)
                        pathPoints = directionsResult?.routes?.firstOrNull()?.overviewPolyline?.decodePath()?.map {
                            LatLng(it.lat, it.lng)
                        } ?: listOf()
                    }
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    // Dibujar el mapa y la ruta
    userLocation?.let {
        GetMapScreen(it, pathPoints)
    }
}

suspend fun getDirections(start: LatLng, end: LatLng, context: Context): DirectionsResult? {
    val apiKey = context.getString(R.string.google_maps_api_key)
    val geoApiContext = GeoApiContext.Builder().apiKey(apiKey).build()

    return try {
        DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING) // MODO caminando , auto , bici , etc.
            .origin(com.google.maps.model.LatLng(start.latitude, start.longitude))
            .destination(com.google.maps.model.LatLng(end.latitude, end.longitude))
            .await()
    } catch (e: Exception) {
        Log.e("GoogleMapsDirections", "Failed to fetch directions", e)
        null
    }
}


@Composable
fun GetMapScreen(userLocation: LatLng, pathPoints: List<LatLng>) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true
        ),
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(userLocation, 10f)
        }
    ) {
        Marker(position = userLocation, title = "Your Location", snippet = "Marker at Your Location")
        if (pathPoints.isNotEmpty()) {
            Polyline(points = pathPoints, color = Color.Blue, width = 5f)
        }
    }
}
