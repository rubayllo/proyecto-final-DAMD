package com.fedeyruben.proyectofinaldamd.ui.alertScreen


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AlertScreenInit() {
    val alerts = listOf(
        "Nivel de alerta bajo" to Icons.Default.Warning,
        "Nivel de alerta medio" to Icons.Default.Warning,
        "Nivel de alerta alto" to Icons.Default.Warning,
        "Nivel de alerta maximo" to Icons.Default.Warning,
    )

    // Estados para manejar la visibilidad y el texto de los diálogos
    var showAlertConfirmDialog by remember { mutableStateOf(false) }
    var currentAlert by remember { mutableStateOf("") }
    var showCountdownDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 72.dp), // Margen inferior para dejar espacio para BottomNavigation
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        alerts.forEachIndexed { index, alert ->
            AlertButton(
                text = alert.first,
                icon = alert.second,
                color = getIconColor(index), // Obtener color diferente para cada icono
                onClick = {
                    currentAlert = alert.first
                    showAlertConfirmDialog = true
                }
            )
        }
    }

    if (showAlertConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showAlertConfirmDialog = false },
            title = { Text("Confirmación") },
            text = { Text("Está por enviar una alerta de $currentAlert.") },
            confirmButton = {
                Button(onClick = {
                    showAlertConfirmDialog = false
                    showCountdownDialog = true
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAlertConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showCountdownDialog) {
        CountdownDialog { showCountdownDialog = false }
    }
}

@Composable
fun AlertButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp) // Reducir la altura de los botones
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start // Icono a la izquierda, texto a la derecha
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(64.dp),
            tint = color // Asignar color al icono
        )
        Spacer(modifier = Modifier.width(16.dp)) // Espacio entre el icono y el texto
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
        )
    }
}


// Función para obtener colores diferentes para cada ícono
fun getIconColor(index: Int): Color {
    return when (index) {
        0 -> Color(0xFF8BC34A)
        1 -> Color(0xFFFFD54F)
        2 -> Color(0xFFF44336)
        3 -> Color(0xFF424242)
        else -> Color.Gray
    }
}


@Composable
fun CountdownDialog(onDismiss: () -> Unit) {
    var countdown by remember { mutableStateOf(10) }
    var showAlertSent by remember { mutableStateOf(false) }
    val backgroundColor = animateColorAsState(
        targetValue = if (countdown % 2 == 0) Color(0xFFD65252) else Color(0xFFC62828) // Oscila entre rojo fuerte y rojo claro
    )
    LaunchedEffect(key1 = true) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        if (!showAlertSent) {
            showAlertSent = true // Cambiar el estado para mostrar el diálogo de alerta enviada
        } else {
            onDismiss() // Cierra el diálogo después de mostrar "ALERTA ENVIADA"
        }
    }
    if (showAlertSent) {
        AlertSentDialog(onDismiss)
    } else {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Enviando Alerta") },
            text = { Text("¡¡¡ESTÁ ENVIANDO UNA ALERTA!!!\nEnviando en $countdown segundos.") },
            confirmButton = {
                Button(
                    onClick = {
                        showAlertSent =
                            true // Asegura que no se muestre el diálogo de alerta enviada si se cancela
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = backgroundColor.value, // Color de fondo animado
            textContentColor = Color.White, // Texto y botones en color blanco
        )
    }
}

@Composable
fun AlertSentDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Alerta Enviada") },
        text = { Text("La alerta ha sido enviada con éxito.") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        dismissButton = { },
    )
}