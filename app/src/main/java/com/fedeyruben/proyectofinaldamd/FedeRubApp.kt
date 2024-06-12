package com.fedeyruben.proyectofinaldamd

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

/** Obtener Token de registro cuando se inicia la aplicacion , para poder enviar notificaciones
 *  push por firebase Messaging*/
@HiltAndroidApp
class FedeRubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.i("MyUserFirebase", "Se ha creado el token $token")
            } else {
                Log.i("MyUserFirebase", "FALLO ${task.exception}")
            }
        }
    }

}