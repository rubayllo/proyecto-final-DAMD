package com.fedeyruben.proyectofinaldamd.ui.customStyleComponents

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fedeyruben.proyectofinaldamd.R

@Preview
@Composable
fun ProgressBarPreview() {
    ProgressBar()
}

@Composable
fun ProgressBar() {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Body(modifier = Modifier)
    }
}


@Composable
private fun Body(modifier: Modifier) {
    FlippingImage(modifier = modifier)

    Text(
        text = "Loading.....",
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(32.dp),
        fontSize = 20.sp
    )
}

@Composable
private fun FlippingImage(modifier: Modifier) {
    val rotationState = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            rotationState.animateTo(360f, tween(1500, easing = LinearEasing))
            rotationState.animateTo(0f, tween(1500, easing = LinearEasing))
        }
    }

    Logo(
        modifier = modifier
            .rotate(rotationState.value)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen() {
    BasicAlertDialog(onDismissRequest = { /* no podener nada para que el usuario no pueda quitarlo */ },
        modifier = Modifier.fillMaxSize(),
        content = {
            FlippingImage(modifier = Modifier.fillMaxSize())
        })
}

@Composable
fun Logo(modifier: Modifier) {
    Image(
        modifier = modifier.fillMaxWidth(0.5f),
        painter = painterResource(id = R.drawable.world),
        contentDescription = "Logo",
        contentScale = ContentScale.FillWidth
    )
}
