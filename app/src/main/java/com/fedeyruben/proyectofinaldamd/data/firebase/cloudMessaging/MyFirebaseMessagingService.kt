package com.fedeyruben.proyectofinaldamd.data.firebase.cloudMessaging

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.ui.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Configura un servicio de escucha de mensajes FCM
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Obtener datos de la notificación
        val title = remoteMessage.notification?.title ?: "Alerta"
        val body = remoteMessage.notification?.body ?: ""
        val latitude = remoteMessage.data["latitude"]?.toDoubleOrNull()
        val longitude = remoteMessage.data["longitude"]?.toDoubleOrNull()

        Log.d("MyFirebaseMessagingService", "Notificación recibida: Title: $title, Body: $body, Lat: $latitude, Long: $longitude")

        // Crear intent para abrir la actividad principal al hacer clic en la notificación
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }

        // Crear la notificación
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(this, "alert_channel")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Mostrar la notificación
        with(NotificationManagerCompat.from(this)) {
            notify(0, notificationBuilder.build())
        }

        // Confirmación de recepción
        sendReceiptConfirmation(latitude, longitude)
    }

    private fun sendReceiptConfirmation(latitude: Double?, longitude: Double?) {
        val userId = getUserId() // Obtener el ID del usuario actual
        val receiptData = hashMapOf(
            "userId" to userId,
            "latitude" to latitude.toString(),
            "longitude" to longitude.toString(),
            "timestamp" to FieldValue.serverTimestamp()
        )
        Firebase.firestore.collection("receipts")
            .add(receiptData)
            .addOnSuccessListener {
                Log.d("MyFirebaseMessagingService", "Confirmación de recepción enviada.")
            }
            .addOnFailureListener { e ->
                Log.e("MyFirebaseMessagingService", "Error al enviar confirmación de recepción: ${e.message}")
            }
    }

    private fun getUserId(): String {
        // Implementar la lógica para obtener el ID del usuario actual
        return "user_id_example"
    }
}

