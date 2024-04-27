package com.fedeyruben.proyectofinaldamd.ui.header

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fedeyruben.proyectofinaldamd.ui.customStyleComponents.LogoSmall

@Composable
fun HeaderRegisterScreen(modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        LogoSmall(modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}