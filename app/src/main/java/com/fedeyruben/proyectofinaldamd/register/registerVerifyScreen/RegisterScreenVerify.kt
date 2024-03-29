package com.fedeyruben.proyectofinaldamd.register.registerVerifyScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.register.viewModel.RegisterViewModel
import com.fedeyruben.proyectofinaldamd.ui.header.Header

@Composable
fun RegisterVerifyScreenInit(navController: NavHostController) {
    RegisterScreenVerify(navController, RegisterViewModel())

}

@Composable
fun RegisterScreenVerify(navController: NavHostController, registerVerifyViewModel: RegisterViewModel) {
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

        Text(
            text = "Verificación de cuenta \n Estoy hasta la poya de hacer ventanas FEDE",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}
