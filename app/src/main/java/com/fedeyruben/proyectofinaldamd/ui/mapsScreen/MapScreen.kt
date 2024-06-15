package com.fedeyruben.proyectofinaldamd.ui.mapsScreen


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fedeyruben.proyectofinaldamd.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.io.IOException


@Composable
fun MapScreenInit(mapViewModel: MapViewModel) {

    val endPoint by mapViewModel.friendAlertLocation.observeAsState()

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cameraPositionState = rememberCameraPositionState()
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var isNavigating by remember { mutableStateOf(false) }
    var instructions by remember { mutableStateOf("") }
    var distance by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var zoomedOut by remember { mutableStateOf(false) }

    // Function to request location permission
    fun requestLocationPermission() {
        val REQUEST_LOCATION_PERMISSION_CODE = 1001
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as ComponentActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION_CODE)
        }
    }

    // Effect to get the user's location
    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    userLocation = currentLatLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 12f)
                }
            }
        } else {
            requestLocationPermission()
        }
    }

    // UI
    Column(modifier = Modifier.fillMaxSize()) {
        NavigationInstruction(instructions)
        Box(modifier = Modifier.weight(1f)) {
            if (endPoint != null) {
                GetMapScreen(userLocation, pathPoints, cameraPositionState, endPoint!!)
                NavigationControls(
                    userLocation = userLocation,
                    pathPoints = pathPoints,
                    endPoint = endPoint!!,
                    cameraPositionState = cameraPositionState,
                    zoomedOut = zoomedOut,
                    isNavigating = isNavigating,
                    onZoomToggle = { zoomedOut = !zoomedOut }
                )
            } else {
                GetMapScreen(userLocation, pathPoints, cameraPositionState, null)
            }
        }
        if (userLocation != null && !isNavigating && endPoint != null) {
            Button(
                onClick = {
                    isNavigating = true
                    startNavigation(userLocation!!, endPoint!!, context, coroutineScope) { newLocation, newPathPoints, newInstruction, newDistance, newDuration ->
                        userLocation = newLocation
                        pathPoints = newPathPoints
                        instructions = newInstruction
                        distance = newDistance
                        duration = newDuration
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 16f)
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Iniciar Navegación")
            }
        }
        NavigationInfo(distance, duration, Modifier.padding(bottom = 56.dp))
    }
}

@Composable
fun GetMapScreen(userLocation: LatLng?, pathPoints: List<LatLng>, cameraPositionState: CameraPositionState, endPoint: LatLng?) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true
        ),
        cameraPositionState = cameraPositionState
    ) {
        endPoint?.let {
            Marker(position = it, title = "Alert Location", snippet = "Location of the alert")
        }
        if (pathPoints.isNotEmpty()) {
            Polyline(points = pathPoints, color = Color.Blue, width = 5f)
        }
    }
}

@Composable
fun NavigationControls(
    userLocation: LatLng?,
    pathPoints: List<LatLng>,
    endPoint: LatLng,
    cameraPositionState: CameraPositionState,
    zoomedOut: Boolean,
    isNavigating: Boolean,
    onZoomToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Botón para alternar entre la vista de usuario y la vista de ruta completa
        IconButton(
            onClick = {
                onZoomToggle()
                if (zoomedOut) {
                    userLocation?.let {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 16f))
                    }
                } else {
                    val bounds = LatLngBounds.builder()
                    pathPoints.forEach { bounds.include(it) }
                    bounds.include(userLocation!!)
                    bounds.include(endPoint)
                    cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.White, shape = RoundedCornerShape(50))
                .padding(8.dp)
        ) {
            // Icono que cambia según el estado de zoom
            Image(
                painter = painterResource(id = if (zoomedOut) R.drawable.ic_arrow else R.drawable.ic_split),
                contentDescription = if (zoomedOut) "Center Map" else "Show Full Route"
            )
        }
    }
}

@Composable
fun NavigationInfo(distance: String, duration: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 30.dp)
    ) {
        Text(
            text = "$duration • $distance",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun NavigationInstruction(instructions: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF000000))
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = instructions,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
fun startNavigation(
    startLocation: LatLng,
    endLocation: LatLng,
    context: Context,
    coroutineScope: CoroutineScope,
    onLocationUpdate: (LatLng, List<LatLng>, String, String, String) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 2000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    // Callback para recibir actualizaciones de ubicación periódicas
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                val newLatLng = LatLng(location.latitude, location.longitude)
                coroutineScope.launch {
                    val directionsResult = getDirections(newLatLng, endLocation, context)
                    val newPathPoints = directionsResult?.routes?.firstOrNull()?.overviewPolyline?.decodePath()?.map {
                        LatLng(it.lat, it.lng)
                    } ?: listOf()
                    val newInstruction = directionsResult?.routes?.firstOrNull()?.legs?.firstOrNull()?.steps?.firstOrNull()?.let {
                        Jsoup.parse(it.htmlInstructions).text()
                    } ?: ""
                    val newDistance = directionsResult?.routes?.firstOrNull()?.legs?.firstOrNull()?.distance?.toString() ?: ""
                    val newDuration = directionsResult?.routes?.firstOrNull()?.legs?.firstOrNull()?.duration?.toString() ?: ""
                    onLocationUpdate(newLatLng, newPathPoints, newInstruction, newDistance, newDuration)
                }
            }
        }
    }

    // Verifica si los permisos de ubicación están concedidos
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Manejo de permisos: si no están concedidos, simplemente retorna
        return
    }

    // Solicita actualizaciones de ubicación periódicas
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

    // Obtener la ubicación actual al iniciar la navegación
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)
            coroutineScope.launch {
                val directionsResult = getDirections(currentLatLng, endLocation, context)
                val initialPathPoints = directionsResult?.routes?.firstOrNull()?.overviewPolyline?.decodePath()?.map {
                    LatLng(it.lat, it.lng)
                } ?: listOf()
                val initialInstruction = directionsResult?.routes?.firstOrNull()?.legs?.firstOrNull()?.steps?.firstOrNull()?.let {
                    Jsoup.parse(it.htmlInstructions).text()
                } ?: ""
                val initialDistance = directionsResult?.routes?.firstOrNull()?.legs?.firstOrNull()?.distance?.toString() ?: ""
                val initialDuration = directionsResult?.routes?.firstOrNull()?.legs?.firstOrNull()?.duration?.toString() ?: ""
                onLocationUpdate(currentLatLng, initialPathPoints, initialInstruction, initialDistance, initialDuration)
            }
        }
    }
}

suspend fun getDirections(start: LatLng, end: LatLng, context: Context): DirectionsResult? {
    val apiKey = context.getString(R.string.google_maps_api_key)
    val geoApiContext = GeoApiContext.Builder().apiKey(apiKey).build()

    return try {
        DirectionsApi.newRequest(geoApiContext)
            .mode(TravelMode.DRIVING)
            .origin(com.google.maps.model.LatLng(start.latitude, start.longitude))
            .destination(com.google.maps.model.LatLng(end.latitude, end.longitude))
            .language("es")
            .await()
    } catch (e: IOException) {
        Log.e("GoogleMapsDirections", "Network error when fetching directions", e)
        null
    } catch (e: Exception) {
        Log.e("GoogleMapsDirections", "Failed to fetch directions", e)
        null
    }
}