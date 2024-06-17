package com.fedeyruben.proyectofinaldamd.ui.alertScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AlertFriendDialog(
    friendName: String,
    onNavigate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Alerta de Peligro") },
        text = { Text(text = "El amigo $friendName está en peligro. ¿Quieres ir a la ubicación?") },
        confirmButton = {
            Button(onClick = onNavigate) {
                Text("Ir a la ubicación")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}
