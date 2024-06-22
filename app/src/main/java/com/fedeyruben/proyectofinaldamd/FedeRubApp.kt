package com.fedeyruben.proyectofinaldamd

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.room.Room
import com.fedeyruben.proyectofinaldamd.data.room.UserDataBase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp


/** Obtener Token de registro cuando se inicia la aplicacion , para poder enviar notificaciones
 *  push por firebase Messaging*/


@HiltAndroidApp
class FedeRubApp : Application() {
    @SuppressLint("StringFormatInvalid")
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FedeRubApp", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d("FedeRubApp", msg)
        }
    }
}
