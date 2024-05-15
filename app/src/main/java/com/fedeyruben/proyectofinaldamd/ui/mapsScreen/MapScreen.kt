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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun GetMapScreen(userLocation: LatLng) {
    Log.d("UBI", "ExploraGo() called with userLocation: $userLocation")
    val startPoint = LatLng(36.6021273, -4.5322362)   // Puedes ajustar este punto como prefieras
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
        if (userLocation != LatLng(0.0, 0.0)) {
            Marker(
                position = userLocation,
                title = "Your Location",
                snippet = "Marker at Your Location"
            )

            /** Aqui deberia crear la linea de viaje */
            // Dibuja una línea desde el punto ficticio hasta la ubicación actual del usuario
            Polyline(
                points = listOf(
                    startPoint,  // Punto inicial
                    userLocation  // Ubicación actual del usuario
                ),
                color = Color.Blue,  // Color de la línea
                width = 5f  // Grosor de la línea
            )

        }
    }
}

@Composable
fun MapScreenInit() {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }


    // Function to request location permission
    fun requestLocationPermission() {
        val REQUEST_LOCATION_PERMISSION_CODE = 1001
        // Verificar si ya se han concedido los permisos
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Los permisos ya han sido concedidos, no hay necesidad de solicitarlos nuevamente
            return
        } else {
            // Los permisos no han sido concedidos, solicitarlos al usuario
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION_CODE
            )
        }
    }

    // Function to get current location
    fun getCurrentLocation(context: Context) {
        Log.d("Location", "getCurrentLocation() called")
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                        /** TODO Ubicacion actual del dispositivo enviar a otros..*/
                        userLocation = LatLng(latitude, longitude)
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(userLocation!!, 15f)
                        Log.d("Location", "User location set: $userLocation")
                        /** TODO
                         * Actualizar ubicacion en FireBase*/
                        val userLocation = UserLocation(location.latitude, location.longitude)
                        val databaseReference = FirebaseDatabase.getInstance().getReference("locations")
                        databaseReference.child("userId").setValue(userLocation)
                    } else {
                        Log.e("Location", "Location is null")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Location", "Error getting location: ${exception.message}")
                }
        } else {
            requestLocationPermission()
        }
    }

    // Ejecución asincrona
    LaunchedEffect(key1 = true) {
        getCurrentLocation(context)
    }

    GetMapScreen( LatLng(36.582245 , -4.534661))

    // Le pasa la ubi a la funcion para crear el mapa
    userLocation?.let {
        Log.d("UBI", "User location obtained: $it")
        GetMapScreen(it)
    }
}