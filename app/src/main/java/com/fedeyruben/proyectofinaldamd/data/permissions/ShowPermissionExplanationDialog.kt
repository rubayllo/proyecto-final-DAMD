package com.fedeyruben.proyectofinaldamd.data.permissions

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fedeyruben.proyectofinaldamd.R

@Composable
fun ShowPermissionExplanationDialog() {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { /* No hacer nada */ },
        title ={ Text(stringResource(id = R.string.permiss_tittle))} ,
        text = { Text(stringResource(id = R.string.permiss_text)) },
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
                Text(text = stringResource(id = R.string.open_settings))
            }
        }
    )
}