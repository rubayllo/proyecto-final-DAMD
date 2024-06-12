package com.fedeyruben.proyectofinaldamd.data.firebase.cloudMessaging


import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.data.dataStore.repository.DataStoreRepository
import com.fedeyruben.proyectofinaldamd.ui.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : FirebaseMessagingService() {

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Obtiene los datos de la notificación y la carga útil
        val title = remoteMessage.notification?.title ?: "Alerta"
        val body = remoteMessage.notification?.body ?: ""
        val latitude = remoteMessage.data["latitude"]?.toDoubleOrNull()
        val longitude = remoteMessage.data["longitude"]?.toDoubleOrNull()

        Log.d("MyFirebaseMessagingService", "Notificación recibida: Title: $title, Body: $body, Lat: $latitude, Long: $longitude")

        // Crea un Intent para abrir la MainActivity cuando se toque la notificación
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }

        // Crea un PendingIntent para el Intent anterior
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Construye la notificación
        val notificationBuilder = NotificationCompat.Builder(this, "alert_channel")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Muestra la notificación
        with(NotificationManagerCompat.from(this)) {
            notify(0, notificationBuilder.build())
        }

        // Envía una confirmación de recepción
        sendReceiptConfirmation(latitude, longitude)
    }

    // Envía una confirmación de recepción a Firestore
    private fun sendReceiptConfirmation(latitude: Double?, longitude: Double?) {
        val userId = getUserId()
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

    // Maneja la actualización del token FCM
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")

        // Guarda el token en Firestore
        sendRegistrationToServer(token)
        // Guarda el token en DataStore
        saveTokenToLocal(token)
    }

    // Guarda el token en Firestore
    private fun sendRegistrationToServer(token: String) {
        val userId = getUserId()
        val userTokenData = hashMapOf(
            "userId" to userId,
            "token" to token
        )
        Firebase.firestore.collection("user_tokens")
            .document(userId)
            .set(userTokenData)
            .addOnSuccessListener {
                Log.d("MyFirebaseMessagingService", "Token guardado en Firestore.")
            }
            .addOnFailureListener { e ->
                Log.e("MyFirebaseMessagingService", "Error al guardar token: ${e.message}")
            }
    }

    // Guarda el token en DataStore
    private fun saveTokenToLocal(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreRepository.saveToken(token)
        }
    }

    // Obtiene el ID del usuario actual
    private fun getUserId(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid ?: "unknown_user"
    }
}
