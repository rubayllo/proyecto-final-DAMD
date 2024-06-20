package com.fedeyruben.proyectofinaldamd.ui.alertScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.fedeyruben.proyectofinaldamd.R

@Composable
fun AlertFriendDialog(
    friendName: String,
    onNavigate: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = stringResource(id = R.string.alerta_peligro),
                textAlign = TextAlign.Center, // Alinea el texto al centro
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.the_friend)
                        + " $friendName " +
                        stringResource(id = R.string.alerta_amigo_peligro)
            )
        },

        confirmButton = {
            Button(onClick = onNavigate) {
                Text(stringResource(id = R.string.go_to_location))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.no))
            }
        }
    )
}

