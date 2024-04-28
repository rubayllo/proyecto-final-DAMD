package com.fedeyruben.proyectofinaldamd.register.registerScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.register.viewModel.RegisterViewModel
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.ButtonStyle
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.OutlineTextFieldStyle
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.textFieldColors
import com.fedeyruben.proyectofinaldamd.ui.header.Header


@Composable

fun RegisterScreenInit(navController: NavHostController) {
    RegisterScreen(navController, RegisterViewModel())
}

@Composable
fun RegisterScreen(navController: NavHostController, registerViewModel: RegisterViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(modifier = Modifier)

        Spacer(modifier = Modifier.size(18.dp))

        BodyRegisterScreen(modifier = Modifier, navController = navController, registerViewModel)
    }
}

@Composable
fun BodyRegisterScreen(
    modifier: Modifier,
    navController: NavHostController,
    registerViewModel: RegisterViewModel
) {

    val dialogOpen = remember { mutableStateOf(false) }
    val enableButton: Boolean by registerViewModel.enableButton.observeAsState(false)

    if (dialogOpen.value) {
        OpenDialog(
            dialogOpen,
            phone = registerViewModel.phone.value,
            codePhone = registerViewModel.codePhone.value,
            navController
        )
    }

    Text(
        modifier = modifier.fillMaxWidth(),
        text = "Ingresa tu número de teléfono",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.size(18.dp))

    Text(
        modifier = modifier.fillMaxWidth(),
        text = "Te enviaremos un mensaje SMS para verificar tu número de teléfono",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.size(50.dp))

    UserPhoneNumber(modifier, registerViewModel)

    Spacer(modifier = Modifier.size(5.dp))

    Text(
        modifier = modifier,
        text = "Puede que tu operador te cobre servicios adicionales por el envío de este SMS",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.size(50.dp))

    ButtonStyle(
        textButton = "Continuar",
        loginEnable = enableButton,
        modifier = modifier,
        onClickAction = { dialogOpen.value = true }
    )


    Spacer(modifier = Modifier.size(18.dp))
}

@Composable
private fun OpenDialog(dialogOpen: MutableState<Boolean>, phone: String?, codePhone: String?, navController: NavHostController) {
    val context= LocalContext.current
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = { dialogOpen.value = false },
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
            val completePhone = "+$codePhone$phone"
            TextButton(
                onClick = {

                    // Realiza acciones de confirmación si es necesario
                    dialogOpen.value = false // Cierra el diálogo
                    RegisterViewModel().onRegister(navController, phone = true, verify = false)
                    RegisterViewModel().startPhoneNumberVerification(completePhone!!,context)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { dialogOpen.value = false }
            ) {
                Text("EDITAR")
            }
        }
    )
}


@Composable
fun UserPhoneNumber(modifier: Modifier, registerViewModel: RegisterViewModel) {

    val numberPhone: String by registerViewModel.phone.observeAsState("")
    val selectedCodePhone: String by registerViewModel.codePhone.observeAsState("__")
    val selectedCountry: String by registerViewModel.country.observeAsState("")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        SelectCountry(selectedCountry) {
            registerViewModel.onCountryChange(it)
        }
    }

    Spacer(modifier = Modifier.size(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            modifier = modifier.width(80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = "+$selectedCodePhone",
                onValueChange = {},
                enabled = false,
                colors = textFieldColors()
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            NumberPhone(numberPhone) {
                registerViewModel.onPhoneChange(it)
            }

        }
    }
}

@Composable
fun NumberPhone(numberPhone: String, onValueChange: (numberPhone: String) -> Unit) {
    OutlinedTextField(
        value = numberPhone,
        onValueChange = { onValueChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = {
            Text(
                text = "Número de teléfono"
            )
        }
    )
}

@Composable
fun SelectCountry(selectedCountry: String, onValueChange: (country: CountriesModel) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    OutlineTextFieldStyle(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true },
        value = selectedCountry,
        label = "País",
        keyboardType = KeyboardType.Text,
        onValueChange = null,
        enabled = false,
        readOnly = true
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .padding(8.dp)
    ) {
        ListOfCountries.orderAlphabetically().forEach { country ->
            DropdownMenuItem(
                { Text(text = country.country) },
                onClick = {
                    expanded = false
                    onValueChange(country)
                },
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}


