package com.fedeyruben.proyectofinaldamd.ui.alertScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedeyruben.proyectofinaldamd.utils.LocationUpdateService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AlertViewModel : ViewModel() {

    // Firebase firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    fun sendAlert(level: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val location = LocationUpdateService.currentLocation.value
                val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber

                if (location != null && phoneNumber != null) {
                    val alertData = hashMapOf(
                        "alertLevel" to level,
                        "geoPoint" to GeoPoint(location.latitude, location.longitude),
                        "isAlert" to true,
                        "phoneNumber" to phoneNumber
                    )

                    val alertsRef = firestore.collection("Alerts").document(phoneNumber)

                    alertsRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Actualiza el documento existente
                                alertsRef.update(alertData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Log.d("sendAlert", "Alerta actualizada: $alertData")
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "sendAlert",
                                            "Error al actualizar la alerta: ${e.message}"
                                        )
                                        onFailure(e)
                                    }
                            } else {
                                // Crea un nuevo documento
                                alertsRef.set(alertData)
                                    .addOnSuccessListener {
                                        Log.d("sendAlert", "Alerta creada: $alertData")
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("sendAlert", "Error al crear la alerta: ${e.message}")
                                        onFailure(e)
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("sendAlert", "Error al obtener el documento: ${e.message}")
                            onFailure(e)
                        }
                } else {
                    onFailure(Exception("Location or phone number not available"))
                }
            } catch (e: Exception) {
                Log.e("sendAlert", "Error: ${e.message}")
                onFailure(e)
            }
        }
    }

    fun cancelAlert(alertLevel: String) {
        viewModelScope.launch {
            try {
                val userPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
                if (userPhoneNumber != null) {
                    val querySnapshot = firestore.collection("Alerts")
                        .whereEqualTo("phoneNumber", userPhoneNumber)
                        .get()
                        .await()

                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        firestore.collection("Alerts").document(document.id)
                            .update(
                                mapOf(
                                    "alertLevel" to null,
                                    "geoPoint" to GeoPoint(0.0, 0.0),
                                    "isAlert" to false
                                )
                            )
                            .addOnSuccessListener {
                                Log.d("cancelAlert", "Alerta cancelada con éxito.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("cancelAlert", "Error al cancelar la alerta: ${e.message}")
                            }
                    } else {
                        Log.e("cancelAlert", "No se encontró el documento con el número de teléfono: $userPhoneNumber")
                    }
                } else {
                    Log.e("cancelAlert", "Número de teléfono no disponible.")
                }
            } catch (e: Exception) {
                Log.e("cancelAlert", "Error: ${e.message}")
            }
        }
    }
}