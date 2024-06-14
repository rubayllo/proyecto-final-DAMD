package com.fedeyruben.proyectofinaldamd.ui.alertScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedeyruben.proyectofinaldamd.utils.LocationUpdateService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AlertViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val _alertStatus = MutableStateFlow(mapOf<String, Boolean>())
    val alertStatus: StateFlow<Map<String, Boolean>> = _alertStatus

    private val _canceled = MutableStateFlow(false)
    val canceled: StateFlow<Boolean> = _canceled

    init {
        _canceled.value = false
    }

    fun resetCanceled() {
        _canceled.value = false
    }

    fun setCanceled(value: Boolean) {
        _canceled.value = value
    }

    fun setAlertStatus(alertLevel: String, value: Boolean) {
        _alertStatus.value = _alertStatus.value.toMutableMap().also { it[alertLevel] = value }
    }

    fun sendAlert(level: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val location = LocationUpdateService.currentLocation.value
                val phoneNumber = auth.currentUser?.phoneNumber

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
                                alertsRef.update(alertData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        onFailure(e)
                                    }
                            } else {
                                alertsRef.set(alertData)
                                    .addOnSuccessListener {
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        onFailure(e)
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                } else {
                    onFailure(Exception("Location or phone number not available"))
                }
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    fun cancelAlert(alertLevel: String) {
        viewModelScope.launch {
            try {
                val userPhoneNumber = auth.currentUser?.phoneNumber
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
