package com.fedeyruben.proyectofinaldamd.services

import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Configura un servicio de escucha de mensajes FCM

val userLocation = mutableStateOf<LatLng?>(null)
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Maneja los mensajes recibidos aquí
        if (remoteMessage.data.isNotEmpty()) {
            val latitude = remoteMessage.data["latitude"]?.toDouble() ?: return
            val longitude = remoteMessage.data["longitude"]?.toDouble() ?: return
            // Procesa la ubicación recibida
            procesarUbicacionActualizacion(latitude, longitude)
        }
    }
    private fun procesarUbicacionActualizacion(latitude: Double, longitude: Double) {
        userLocation.value = LatLng(latitude, longitude)
    }
}
