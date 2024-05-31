package com.fedeyruben.proyectofinaldamd.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationUpdateService : Service() {

    // Cliente de ubicación para obtener actualizaciones de la ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Configuración de las solicitudes de ubicación
    private lateinit var locationRequest: LocationRequest

    // Callback para manejar los resultados de las actualizaciones de ubicación
    private lateinit var locationCallback: LocationCallback

    companion object {
        // LiveData para emitir actualizaciones de la ubicación actual
        private val _currentLocation = MutableLiveData<LatLng>()
        val currentLocation: LiveData<LatLng> get() = _currentLocation
    }

    override fun onCreate() {
        super.onCreate()

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configura la solicitud de ubicación
        locationRequest = LocationRequest.create().apply {
            interval = 5000 // Intervalo de tiempo entre solicitudes de ubicación (5 segundos)
            fastestInterval = 2000 // Intervalo más rápido entre solicitudes (2 segundos)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Alta precisión
        }

        // Define el callback para manejar los resultados de las actualizaciones de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                // Itera a través de todas las ubicaciones recibidas
                for (location in locationResult.locations) {
                    // Ejecuta una corrutina en el contexto IO para operaciones en segundo plano
                    CoroutineScope(Dispatchers.IO).launch {
                        val newLatLng = LatLng(location.latitude, location.longitude)
                        // Actualiza la ubicación en la UI principal utilizando LiveData
                        withContext(Dispatchers.Main) {
                            _currentLocation.postValue(newLatLng)
                            // Aquí puedes recalcular la ruta si es necesario
                        }
                    }
                }
            }
        }

        // Inicia las actualizaciones de ubicación
        startLocationUpdates()

        // Inicia el servicio en primer plano para evitar que el sistema lo mate
        startForegroundService()
    }

    // Método para iniciar las actualizaciones de ubicación
    private fun startLocationUpdates() {
        // Verifica si los permisos de ubicación están concedidos
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Manejo de permisos: si no están concedidos, simplemente retorna
            return
        }
        // Solicita actualizaciones de ubicación al cliente de ubicación
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    // Método para iniciar el servicio en primer plano
    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val channelId = "location_update_channel" // ID del canal de notificaciones
        val channelName = "Location Update Service" // Nombre del canal de notificaciones
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        // Intent para abrir la actividad principal al hacer clic en la notificación
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_MUTABLE)

        // Construye la notificación del servicio en primer plano
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Location Service")
            .setContentText("Updating location in background")
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .build()

        // Inicia el servicio en primer plano con la notificación
        startForeground(1, notification)
    }

    // Método requerido para el servicio vinculado (no utilizado en este caso)
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Método llamado cuando el servicio se destruye
    override fun onDestroy() {
        super.onDestroy()
        // Detiene las actualizaciones de ubicación cuando el servicio se destruye
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
