package com.fedeyruben.proyectofinaldamd.maps

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
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun GetMapScreen(userLocation: LatLng) {
    Log.d("UBI", "ExploraGo() called with userLocation: $userLocation")
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
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
        }
    }
}

@Composable
fun MapScreenInit(navController: NavHostController) {
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
                    /** TODO LLEGA NULL la Ubi **/
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                        userLocation = LatLng(latitude, longitude)
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(userLocation!!, 15f)
                        Log.d("Location", "User location set: $userLocation")
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

    /** Prueba el mapa funciona */
    // GetMapScreen(LatLng(40.7128, -74.0060))

    // Le pasa la ubi a la funcion para crear el mapa
    userLocation?.let {
        Log.d("UBI", "User location obtained: $it")
        GetMapScreen(it)
    }
}


