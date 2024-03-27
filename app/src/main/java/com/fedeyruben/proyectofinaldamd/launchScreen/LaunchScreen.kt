package com.fedeyruben.proyectofinaldamd.launchScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.navigation.AppScreensRoutes
import kotlinx.coroutines.delay

@Composable
fun LaunchScreenInit(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(5000)
        navController.popBackStack()
        navController.navigate(AppScreensRoutes.MapScreen.route)
    }
    LaunchScreen()
}

@Composable
fun LaunchScreen() {
    Column(
        Modifier
            .fillMaxSize()
            //.background(Color.Black)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlippingImage()
    }
}

@Composable
private fun FlippingImage() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Controlador de posición horizontal del avión
    val positionX = remember { Animatable(300f) }

    // Controlador de posición vertical del avión
    val positionY = remember { Animatable(100f) }

    // Controlador de rotación continua del mundo
    val rotationState = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            // Animación de rotación continua del mundo
            rotationState.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(5000),
                    repeatMode = RepeatMode.Restart
                )

            )
            delay(40000)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            // Animación de movimiento de derecha a izquierda (más lento)
            positionX.animateTo(
                targetValue = 0f,
                animationSpec = tween(5000)
            )
            // Pequeña pausa cuando el avión llega al centro del mundo
            //delay(1000)
            // Animación de desaparición del avión
            positionX.animateTo(
                targetValue = -200f,
                animationSpec = TweenSpec(durationMillis = 1)
            )
            // Reiniciar la posición del avión
            positionX.snapTo(300f)
            delay(1000) // Pequeño retraso para asegurar que la animación se haya completado antes de reiniciar
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
        //.background(Color.Black)
    ) {
        // Imagen del mundo (más grande)
        Image(
            painter = painterResource(id = R.drawable.world),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                // Ajuste de tamaño del mundo
                .size(280.dp)
                // Rotación continua del mundo
                .rotate(rotationState.value)
        )

        // Imagen del avión (más grande)
        Image(
            painter = painterResource(id = R.drawable.plane5),
            contentDescription = null,
            modifier = Modifier
                .offset(
                    x = positionX.value.dp,
                    y = positionY.value.dp
                ) // Ajuste de posición del avión
                // Ajuste de tamaño del avión
                .size(300.dp)
                // Hacer que el avión sea visible solo cuando su posición X no es -200f
                .alpha(if (positionX.value != -200f) 1f else 0f)
        )
    }
}

