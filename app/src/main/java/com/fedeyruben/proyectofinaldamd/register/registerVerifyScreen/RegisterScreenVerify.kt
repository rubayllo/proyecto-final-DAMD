package com.fedeyruben.proyectofinaldamd.register.registerVerifyScreen

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.register.registerScreen.NumberPhone
import com.fedeyruben.proyectofinaldamd.register.registerScreen.SelectCountry
import com.fedeyruben.proyectofinaldamd.register.viewModel.RegisterViewModel
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.ButtonStyle
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.textFieldColors
import com.fedeyruben.proyectofinaldamd.ui.header.Header

@Composable
fun RegisterVerifyScreenInit(navController: NavHostController) {
    RegisterScreenVerify(navController, RegisterViewModel())

}

@Composable
fun RegisterScreenVerify(
    navController: NavHostController,
    registerVerifyViewModel: RegisterViewModel
) {
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

        BodyRegisterScreenVerify(
            modifier = Modifier,
            navController = navController,
            registerVerifyViewModel
        )
    }
}

@Composable
fun BodyRegisterScreenVerify(
    modifier: Modifier,
    navController: NavHostController,
    registerVerifyViewModel: RegisterViewModel
) {
    val enableButton: Boolean by registerVerifyViewModel.enableButton.observeAsState(false)

    TitleAndInfoRegisterScreenVerify(modifier)

    Spacer(modifier = Modifier.size(18.dp))

    VerifyCode(modifier, registerVerifyViewModel)

    Spacer(modifier = Modifier.size(18.dp))

    ButtonStyle(
        textButton = "Continuar",
        loginEnable = enableButton,
        modifier = modifier,
        onClickAction = { RegisterViewModel().onRegister(navController,
            phone = false,
            verify = true
        ) }
    )

}

@Composable
fun VerifyCode(modifier: Modifier, registerViewModel: RegisterViewModel) {

    val verifyCode: String by registerViewModel.verifyCode.observeAsState("")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        OutlinedTextField(
            value = verifyCode,
            onValueChange = {registerViewModel.verifyPhoneNumberWithCode(it)},
            //onValueChange = { registerViewModel.onVerifyCodeChange(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = {
                Text(
                    text = "Codigo de verificación",
                )
            }
        )
        Text(text = "Ingresa el código de 6 dígitos")
    }
}

@Composable
private fun TitleAndInfoRegisterScreenVerify(modifier: Modifier) {
    Text(
        modifier = modifier.fillMaxWidth(),
        //TODO: poner numero de telefono introducido anteriormente cuando este implementado DataStore
        text = "Verificar número de teléfono",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )

    Spacer(modifier = Modifier.size(18.dp))

    Text(
        modifier = modifier.fillMaxWidth(),
        text = "Una vez recbido el código de verificación, introdúcelo a continuación",
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
    )
}
