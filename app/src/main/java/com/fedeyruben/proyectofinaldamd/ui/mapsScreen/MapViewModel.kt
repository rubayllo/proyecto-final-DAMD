package com.fedeyruben.proyectofinaldamd.ui.mapsScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fedeyruben.proyectofinaldamd.data.dataStore.repository.DataStoreRepository
import com.fedeyruben.proyectofinaldamd.data.room.UserDatabaseDaoRepositoryImp
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    private val _friendAlertLocation = MutableLiveData<LatLng?>()
    val friendAlertLocation: LiveData<LatLng?> = _friendAlertLocation

    init {
        listenForFriendsAlerts()
    }

    private fun listenForFriendsAlerts() {
        val userPhoneNumber = auth.currentUser?.phoneNumber
        userPhoneNumber?.let {
            firestore.collection("Alerts")
                .whereEqualTo("isAlert", true)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.i("MapViewModel", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    for (doc in snapshots!!) {
                        val geoPoint = doc.getGeoPoint("geoPoint")
                        if (geoPoint != null) {
                            val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                            _friendAlertLocation.postValue(latLng)
                        }
                    }
                }
        }
    }
}
