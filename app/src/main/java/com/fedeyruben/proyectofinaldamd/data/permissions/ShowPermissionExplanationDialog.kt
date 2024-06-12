package com.fedeyruben.proyectofinaldamd.data.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
    fun ShowPermissionExplanationDialog() {
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { /* No hacer nada */ },
            title = { Text("Permisos necesarios") },
            text = {
                Text(
                    "Necesitamos acceso a tu ubicación, contactos y al estado de tú dispositivo móvil para proporcionarte una mejor experiencia. " +
                            "Por favor, concede los permisos en la configuración de la aplicación."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Abrir la configuración de la aplicación
                        val settingsIntent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(settingsIntent)
                    }
                ) {
                    Text("Abrir configuración")
                }
            }
        )
    }