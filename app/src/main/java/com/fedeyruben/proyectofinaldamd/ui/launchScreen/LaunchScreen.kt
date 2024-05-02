package com.fedeyruben.proyectofinaldamd.ui.launchScreen

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.ui.navigation.AppScreensRoutes
import kotlinx.coroutines.delay


@Composable
fun LaunchScreenInit(navController: NavHostController) {
    LaunchedEffect(key1 = true) {
        delay(88000)
        navController.popBackStack()
        navController.navigate(AppScreensRoutes.RegisterScreen.route)
    }
    LaunchScreen()
}

@Composable
fun LaunchScreen() {
    Column(
        Modifier
            .fillMaxSize()
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

    // Controlador de posición horizontal de la persona
    val positionX = remember { Animatable(500f) } // Comienza fuera de la pantalla por la derecha

    // Controlador de posición vertical de la persona
    val positionY = remember { Animatable(130f) }

    // Controlador de rotación continua del mundo
    val rotationState = remember { Animatable(0f) }

    // Estado para controlar la visibilidad de la imagen de alerta
    val showAlert = remember { mutableStateOf(false) }

    // Estado para controlar la visibilidad de la linea
    val showLine = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = "worldRotation") {
        // Anima la rotación del mundo solo una vez hasta 360 grados
        rotationState.animateTo(
            targetValue = 360f,
            animationSpec = tween(5000)
        )
    }

    LaunchedEffect(key1 = "personMovement") {
        delay(3000)
        // Detener el hombre justo en el borde del mundo
        // El valor ajustado depende del ancho del hombre y del mundo
        val stopPositionX = screenWidth / 2 + 55.dp

        positionX.animateTo(
            targetValue = stopPositionX.value,
            animationSpec = tween(durationMillis = 2000)
        )
        // Muestra la alerta cuando el hombre se detiene
        showAlert.value = true
        delay(1500)
        showLine.value = true


    }

    Box(modifier = Modifier.fillMaxSize()) {


        // Imagen del mundo
        Image(
            painter = painterResource(id = R.drawable.world),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .size(280.dp)
                .rotate(rotationState.value)
        )

        // Imagen del hombre
        Image(
            painter = painterResource(id = R.drawable.man),
            contentDescription = null,
            modifier = Modifier
                .offset(x = positionX.value.dp, y = positionY.value.dp)
        )

        // Imagen de la alerta, ajustada 30px más a la izquierda dentro del mundo
        if (showAlert.value) {
            Image(
                painter = painterResource(id = R.drawable.alert),
                contentDescription = "Alert",
                modifier = Modifier
                    // Mueve la alarma 30px más a la izquierda asegurándose de que esté dentro del mundo
                    .offset(
                        x = (screenWidth / 2 - 70.dp),
                        y = 60.dp
                    ) // Ajustado 30px más a la izquierda desde la posición anterior
                    .size(50.dp)
            )
        }

        if (showLine.value) {

            // Asegúrate de que 'screenWidthDp' y 'screenHeightDp' sean del tipo correcto.
            val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
            val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp

            val centerX = screenWidthDp / 2
            val centerY = screenHeightDp / 2

            // Ajusta los puntos para que la línea comience y termine cerca del centro en Dp
            val personCenterXDp = centerX - 50.dp // Restar 50.dp al centro X
            val personCenterYDp = centerY - 300.dp + 25.dp // Centro Y
            val alertCenterXDp = centerX + 50.dp // Sumar 50.dp al centro X
            val alertCenterYDp = centerY - 250.dp + 25.dp

            // Incrementa el valor de endYDp para terminar justo debajo de la alarma
            val adjustedEndYDp = alertCenterYDp + 20.dp // Sumar 10.dp para bajar la línea justo debajo de la alarma

            DottedLine(
                startXDp = personCenterXDp,
                startYDp = personCenterYDp,
                endXDp = alertCenterXDp ,
                endYDp = adjustedEndYDp// Usa el nuevo valor ajustado
            )
        }
    }
}

//*
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DottedLine(startXDp: Dp, startYDp: Dp, endXDp: Dp, endYDp: Dp) {

    val density = LocalDensity.current

    // Conversiones de dp a px
    val startX = with(density) { startXDp.toPx() }
    val startY = with(density) { startYDp.toPx() }
    val endX = with(density) { endXDp.toPx() }
    val endY = with(density) { endYDp.toPx() }
    val strokeWidthPx = with(density) { 4.dp.toPx() }
    val curveAdjustmentPx = with(density) { -100.dp.toPx() }

    // Animar la fase del PathEffect
    val pathEffectPhase = remember { Animatable(0f) }
    LaunchedEffect(key1 = true) {
        pathEffectPhase.animateTo(
            targetValue = 1000f, // Un valor grande para asegurar que la línea se dibuje completamente
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )
    }

    BoxWithConstraints {
        val density = LocalDensity.current

        // Convierte dp a px basado en el tamaño actual de BoxWithConstraints
        val startX = with(density) { startXDp.toPx() }
        val startY = with(density) { startYDp.toPx() }
        val endX = with(density) { endXDp.toPx() }
        val endY = with(density) { endYDp.toPx() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(startX, startY)
                val midPointX = (startX + endX) / 2
                val midPointY = (startY + endY) / 2 - curveAdjustmentPx
                cubicTo(startX, startY, midPointX, midPointY, endX, endY)
            }
            val pathEffect =
                PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase = pathEffectPhase.value)
            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = strokeWidthPx, pathEffect = pathEffect)
            )
        }
    }
}






