package com.fedeyruben.proyectofinaldamd.data.permissions

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
fun SystemBroadcastReceiver(
    systemAction: String, // Acción del sistema que se escuchará
    onSystemEvent: (Intent: Intent?) -> Unit // Función de devolución de llamada que se ejecutará cuando se reciba el evento del sistema
) {
    val content = LocalContext.current // Obtiene el contexto actual del composable

    val currentOnSystemEvent = rememberUpdatedState(onSystemEvent) // Recuerda el estado actual de la función de devolución de llamada

    DisposableEffect(content, systemAction) { // Efecto desechable que se ejecuta cuando el composable se inicia o se actualiza
        val intentFilter = IntentFilter(systemAction) // Crea un filtro de intentos para la acción del sistema especificada
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentOnSystemEvent.value(intent) // Cuando se recibe un intento del sistema, se llama a la función de devolución de llamada con el intento recibido
            }
        }
        content.registerReceiver(broadcastReceiver, intentFilter) // Registra el receptor de transmisiones con el filtro de intención en el contexto actual

        onDispose {
            content.unregisterReceiver(broadcastReceiver) // Cuando el composable se desecha, se anula el registro del receptor de transmisiones
        }
    }
}