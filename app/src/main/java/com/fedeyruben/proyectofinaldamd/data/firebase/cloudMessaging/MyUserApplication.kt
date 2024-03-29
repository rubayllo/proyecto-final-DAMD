package com.fedeyruben.proyectofinaldamd.data.firebase.cloudMessaging

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

/** Obtener Token de registro cuando se inicia la aplicacion , para poder enviar notificaciones
 *  push por firebase Messaging*/
class MyUserApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val tag = "MyUserFirebase"
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token =
                    task.result  // Guardo el token para saber que usuarios estan activos y cuales no para enviar notificaciones
                Log.i("$tag", "Se ha creado el token $token")
            } else {
                Log.i("$tag", "FALLO ${task.exception}")
            }
        }
    }
}