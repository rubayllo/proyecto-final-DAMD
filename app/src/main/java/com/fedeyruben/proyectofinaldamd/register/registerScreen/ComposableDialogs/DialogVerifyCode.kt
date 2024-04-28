package com.fedeyruben.proyectofinaldamd.register.registerScreen.ComposableDialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.fedeyruben.proyectofinaldamd.register.viewModel.RegisterViewModel

@Composable
fun DialogVerifyCode(registerViewModel: RegisterViewModel) {

    val verifyCode: String by registerViewModel.verifyCode.observeAsState("")

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {
            //No hacer nada
        },
        title = {
            Text("Verificar número de teléfono")
        },
        text = {
            Column {
                Text("Ingresa el código de verificación")
                OutlinedTextField(
                    value = verifyCode,
                    onValueChange = { registerViewModel.onVerifyCodeChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    label = {
                        Text(
                            text = "Codigo de verificación",
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    registerViewModel.signInCode(verifyCode)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { registerViewModel.dialogCodeOpen(false) }
            ) {
                Text("CANCELAR")
            }
        }
    )
}