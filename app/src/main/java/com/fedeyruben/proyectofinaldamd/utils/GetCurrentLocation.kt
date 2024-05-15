package com.fedeyruben.proyectofinaldamd.utils

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.Manifest
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await // Si estás usando 'await', asegúrate de tener la dependencia de kotlin coroutines para firebase

internal object LocationService {
    suspend fun getCurrentLocation(context: Context): Location {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        when {
            !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> throw LocationServiceException.LocationDisabledException()
            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> throw LocationServiceException.NoNetworkEnabledException()
            else -> {
                val locationProvider = LocationServices.getFusedLocationProviderClient(context)
                val request = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                    .build()

                runCatching {
                    val location = if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        throw LocationServiceException.MissingPermissionException()
                    } else {
                        locationProvider.getCurrentLocation(request, null).await()
                    }
                    return location
                }.getOrElse {
                    throw LocationServiceException.UnknownException(stace = it.stackTraceToString())
                }
            }
        }
    }
    sealed class LocationServiceException : Exception() {
        class MissingPermissionException : LocationServiceException()
        class LocationDisabledException : LocationServiceException()
        class NoNetworkEnabledException : LocationServiceException()
        class UnknownException(val stace: String) :LocationServiceException()
    }

}