package com.fedeyruben.proyectofinaldamd.ui.registerScreen.registerScreen.ComposableDialogs

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.viewModel.RegisterViewModel

@Composable
fun OpenConfirmPhoneDialog(
    dialogConfirmPhone: MutableState<Boolean>,
    phone: String?,
    codePhone: String?,
    registerViewModel: RegisterViewModel
) {
    Log.d("PHONE1", "Phone number1: +$codePhone$phone")
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = { dialogConfirmPhone.value = false },
        title = {
            Text(
                text = "Vamos a verificar el número de teléfono",
                textAlign = TextAlign.Center, // Alinea el texto al centro
            )
        },
        text = {
            Text(
                text = "+$codePhone $phone \n ¿Es correcto o quieres modificarlo?",
                textAlign = TextAlign.Center, // Alinea el texto al centro
            )

        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Realiza acciones de confirmación si es necesario
                    dialogConfirmPhone.value = false // Cierra el diálogo
                    registerViewModel.onConfirmPhone(
                        phone = true,
                        phoneNumber = "+$codePhone$phone")
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { dialogConfirmPhone.value = false }
            ) {
                Text("EDITAR")
            }
        }
    )
}