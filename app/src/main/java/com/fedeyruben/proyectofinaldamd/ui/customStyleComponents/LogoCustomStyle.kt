package com.fedeyruben.proyectofinaldamd.ui.customStyleComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.fedeyruben.proyectofinaldamd.R

@Composable
fun LogoHigh(modifier: Modifier) {
    Image(
        modifier = modifier.fillMaxWidth(0.5f),
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "logo OMKROM",
        contentScale = ContentScale.FillWidth
    )
}

@Composable
fun LogoSmall(modifier: Modifier) {
    Image(
        modifier = modifier.fillMaxWidth(0.3f),
        painter = painterResource(id = R.drawable.logo),
        contentDescription = "logo OMKROM",
        contentScale = ContentScale.FillWidth
    )
}

// Path: app/src/main/res/drawable/logo.xml