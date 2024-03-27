package com.fedeyruben.proyectofinaldamd.registerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.navigation.AppScreensRoutes
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.ButtonStyle
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.LogoSmall
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.OutlineTextFieldStyle
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.OutlineTextFieldPasswordStyle

@Preview(showBackground = true, showSystemUi = true, name = "Register Screen")
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    RegisterScreenInit(navController = navController)
}

@Composable
fun RegisterScreenInit(navController: NavHostController) {
    RegisterScreen(navController = navController)
}

@Composable
fun RegisterScreen(navController: NavHostController) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(modifier = Modifier)

        Spacer(modifier = Modifier.size(18.dp))

        Body(navController = navController)
    }
}

@Composable
private fun Body(navController: NavHostController) {
    // TODO hacer comprobaciones de password para que contenga
    //  Min 9 caract, 1 número, 1 Mayúscula y 1 símbolo

    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var passwordConfirm by rememberSaveable {
        mutableStateOf("")
    }
    var name by rememberSaveable {
        mutableStateOf("")
    }
    var lastName by rememberSaveable {
        mutableStateOf("")
    }
    var phone by rememberSaveable {
        mutableStateOf("")
    }
    var adress by rememberSaveable {
        mutableStateOf("")
    }
    var floor by rememberSaveable {
        mutableStateOf("")
    }
    var door by rememberSaveable {
        mutableStateOf("")
    }
    var stair by rememberSaveable {
        mutableStateOf("")
    }
    var postalCode by rememberSaveable {
        mutableStateOf("")
    }
    var city by rememberSaveable {
        mutableStateOf("")
    }
    var country by rememberSaveable {
        mutableStateOf("")
    }

    Column {
        Text(text = "Datos de Acesso:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldStyle(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            label = "Email",
            leadingIcon = Icons.Default.MailOutline,
            keyboardType = KeyboardType.Email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldPasswordStyle(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            label = "Password",
            onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldPasswordStyle(
            modifier = Modifier.fillMaxWidth(),
            value = passwordConfirm,
            label = "Repetir Password",
            onValueChange = { passwordConfirm = it }
        )

        Spacer(modifier = Modifier.size(24.dp))
        Text(text = "Datos Personales:", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldStyle(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            label = "Nombre",
            leadingIcon = Icons.Filled.AccountCircle,
            keyboardType = KeyboardType.Text,
            onValueChange = { name = it }
        )

        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldStyle(
            modifier = Modifier.fillMaxWidth(),
            value = lastName,
            label = "Apellidos",
            leadingIcon = Icons.Filled.AccountCircle,
            keyboardType = KeyboardType.Text,
            onValueChange = { lastName = it }
        )

        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldStyle(
            modifier = Modifier.fillMaxWidth(),
            value = phone,
            label = "Teléfono",
            leadingIcon = Icons.Filled.Phone,
            keyboardType = KeyboardType.Phone,
            onValueChange = { phone = it }
        )


        Spacer(modifier = Modifier.size(24.dp))
        Text(text = "Dirección:", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldStyle(
            modifier = Modifier.fillMaxWidth(),
            value = adress,
            label = "Calle",
            leadingIcon = Icons.Filled.Place,
            keyboardType = KeyboardType.Text,
            onValueChange = { adress = it }
        )

        Spacer(modifier = Modifier.size(14.dp))
        Row {
            OutlineTextFieldStyle(
                modifier = Modifier.weight(1f),
                value = floor,
                label = "Piso",
                leadingIcon = Icons.Filled.Apartment,
                keyboardType = KeyboardType.Text,
                onValueChange = { floor = it }
            )
            Spacer(modifier = Modifier.size(16.dp))
            OutlineTextFieldStyle(
                modifier = Modifier.weight(1f),
                value = door,
                label = "Puerta",
                leadingIcon = Icons.Filled.DoorFront,
                keyboardType = KeyboardType.Text,
                onValueChange = { door = it }
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Row {
            OutlineTextFieldStyle(
                modifier = Modifier.weight(1f),
                value = stair,
                label = "Escalera",
                leadingIcon = Icons.Filled.Stairs,
                keyboardType = KeyboardType.Text,
                onValueChange = { stair = it }
            )

            Spacer(modifier = Modifier.size(16.dp))
            OutlineTextFieldStyle(
                modifier = Modifier.weight(1f),
                value = postalCode,
                label = "C.P.",
                leadingIcon = Icons.Filled.LocalPostOffice,
                keyboardType = KeyboardType.Number,
                onValueChange = { postalCode = it }
            )
        }

        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldStyle(
            modifier = Modifier.fillMaxWidth(),
            value = city,
            label = "Ciudad",
            leadingIcon = Icons.Filled.LocationCity,
            keyboardType = KeyboardType.Text,
            onValueChange = { city = it }
        )

        Spacer(modifier = Modifier.size(16.dp))
        OutlineTextFieldStyle(
            modifier = Modifier.fillMaxWidth(),
            value = country,
            label = "País",
            leadingIcon = Icons.Filled.Public,
            keyboardType = KeyboardType.Text,
            onValueChange = { country = it }
        )


        Spacer(modifier = Modifier.size(30.dp))

        RegisterButton(navController = navController)

    }
}

@Composable
fun RegisterButton(navController: NavHostController) {
    val isEnable by rememberSaveable {
        mutableStateOf(false)
    }
    ButtonStyle(
        textButton = "Registrar",
        loginEnable = isEnable,
        onClickAction = {
            navController.navigate(AppScreensRoutes.LoginScreen.route)
        })
}

@Composable
private fun Header(modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        LogoSmall(modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Registrate",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
    }
}