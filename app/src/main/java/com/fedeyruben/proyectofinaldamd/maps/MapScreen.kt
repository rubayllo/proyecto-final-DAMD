package com.fedeyruben.proyectofinaldamd.maps


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreenInit() {
    val context = LocalContext.current
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    )

    // Este efecto se lanzará cada vez que el estado de los permisos cambie.
    LaunchedEffect(key1 = true) {
        // Solicitar permisos solo si aún no se han concedido.
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var showRationaleDialog by remember { mutableStateOf(false) }


    permissionsState.permissions.forEach { perm ->
        when (perm.permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                when {
                    perm.status.isGranted -> {
                        // Permiso concedido, podemos obtener la ubicación.
                        getCurrentLocation(fusedLocationClient) { location ->
                            userLocation = location
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(location, 15f)
                        }
                    }

                    perm.status.shouldShowRationale -> {
                        // Se debe mostrar una justificación para el permiso.
                        showRationaleDialog = true
                    }

                    else -> {
                        // El permiso ha sido denegado. .
                        // Redirigiremos al usuario a la configuración de la app.
                        openSettings(context)
                    }
                }
            }
        }
    }


    // Diálogo de justificación de permisos
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("Permiso de ubicación requerido") },
            text = { Text("Esta aplicación necesita el permiso de ubicación para mostrar su posición en el mapa.") },
            confirmButton = {
                Button(
                    onClick = {
                        showRationaleDialog = false
                        permissionsState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Permitir")
                }
            }
        )
    }

    // UI de Mapa
    userLocation?.let { location ->
        GetMapScreen(location)
    } ?: run {
        // Mostrar alguna UI que indique que la ubicación no está disponible
        Text("Ubicación no disponible, por favor asegúrate de que los permisos están concedidos y tu ubicación está encendida.")
    }
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationFound: (LatLng) -> Unit
) {
    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationFound(LatLng(location.latitude, location.longitude))
        } else {
            // Manejar el caso en que location es null
            Log.e("GetMapScreen", "No se pudo obtener la ubicación.")
        }
    }.addOnFailureListener { exception ->
        // Manejar el caso de error
        Log.e("GetMapScreen", "Error al obtener la ubicación", exception)
    }
}

fun openSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

@Composable
fun GetMapScreen(userLocation: LatLng) {
    Log.d("UBI", "ExploraGo() called with userLocation: $userLocation")
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
        }
    }
}

