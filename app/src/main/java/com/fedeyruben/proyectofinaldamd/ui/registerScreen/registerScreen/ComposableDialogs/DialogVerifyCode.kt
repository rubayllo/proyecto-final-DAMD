package com.fedeyruben.proyectofinaldamd.ui.registerScreen.registerScreen.ComposableDialogs

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
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.LoadingScreen
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.viewModel.RegisterViewModel

@Composable
fun DialogVerifyCode(registerViewModel: RegisterViewModel) {
    val isLoading by registerViewModel.isLoading.observeAsState(false)
    val verifyCode: String by registerViewModel.verifyCode.observeAsState("")

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {
            // No hacer nada
        },
        title = {
            Text("Verificar número de teléfono")
        },
        text = {
            if (isLoading) {
                LoadingScreen()
            } else {
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
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    registerViewModel.signInCode(verifyCode)
                },
                enabled = !isLoading
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { registerViewModel.dialogCodeOpen(false) },
                enabled = !isLoading
            ) {
                Text("CANCELAR")
            }
        }
    )
}


@Composable
fun DialogIncorrectCode(registerViewModel: RegisterViewModel) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = {
        },
        title = {
            Text("Código incorrecto, vuelva a intentarlo")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    registerViewModel.showDialogIncorrectCode(false)
                    registerViewModel.cleanVerifyCode()
                }
            ) {
                Text("OK")
            }
        },
    )
}