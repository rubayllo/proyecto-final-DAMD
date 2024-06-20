package com.fedeyruben.proyectofinaldamd.ui.alertScreen


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fedeyruben.proyectofinaldamd.R
import kotlinx.coroutines.delay

@Composable
fun AlertScreenInit(alertViewModel: AlertViewModel ) {
    val alerts = listOf(
        "Nivel de alerta bajo" to Icons.Default.Warning,
        "Nivel de alerta medio" to Icons.Default.Warning,
        "Nivel de alerta alto" to Icons.Default.Warning,
        "Nivel de alerta máximo" to Icons.Default.Warning,
    )

    val alertStatus by alertViewModel.alertStatus.collectAsState()
    var showAlertConfirmDialog by remember { mutableStateOf(false) }
    var showCancelAlertDialog by remember { mutableStateOf(false) }
    var currentAlert by remember { mutableStateOf("") }
    var showCountdownDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        alerts.forEachIndexed { index, alert ->
            val alertSent = alertStatus[alert.first] ?: false
            AlertButton(
                // TODO comprobar posible error
                text = (if (alertSent) R.string.alert_sent else alert.first).toString(),
                icon = alert.second,
                color = getIconColor(index),
                onClick = {
                    currentAlert = alert.first
                    if (alertSent) {
                        showCancelAlertDialog = true
                    } else {
                        showAlertConfirmDialog = true
                    }
                }
            )
        }
    }

    if (showAlertConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showAlertConfirmDialog = false },
            title = { R.string.alert_confirm_title },
            text = { R.string.alert_confirm_text.toString() + " $currentAlert."},
            confirmButton = {
                Button(onClick = {
                    showAlertConfirmDialog = false
                    showCountdownDialog = true
                    alertViewModel.resetCanceled()  // Reset canceled state
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))) {
                    R.string.confirm
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAlertConfirmDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    R.string.cancel
                }
            }
        )
    }

    if (showCountdownDialog) {
        CountdownDialog(alertViewModel, currentAlert) { showCountdownDialog = false }
    }

    if (showCancelAlertDialog) {
        AlertDialog(
            onDismissRequest = { showCancelAlertDialog = false },
            title = { R.string.cancel_alert_title },
            text = { R.string.cancel_alert_text.toString() + " $currentAlert." },
            confirmButton = {
                Button(onClick = {
                    alertViewModel.setAlertStatus(currentAlert, false)
                    alertViewModel.cancelAlert(currentAlert)
                    showCancelAlertDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))) {
                    R.string.yes
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCancelAlertDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    R.string.no
                }
            }
        )
    }
}

@Composable
fun CountdownDialog(alertViewModel: AlertViewModel, alertLevel: String, onDismiss: () -> Unit) {
    var countdown by remember { mutableStateOf(10) }
    var showAlertSent by remember { mutableStateOf(false) }
    val canceled by alertViewModel.canceled.collectAsState()

    LaunchedEffect(key1 = true) {
        while (countdown > 0 && !canceled) {
            delay(1000)
            countdown--
        }
        if (!canceled) {
            alertViewModel.sendAlert(alertLevel, {
                Log.i("ALERT", "Alerta enviada con éxito")
                showAlertSent = true
                alertViewModel.setAlertStatus(alertLevel, true)
                onDismiss()
            }, { error ->
                Log.e("ALERT", "Error al enviar la alerta: ${error.localizedMessage}")
                showAlertSent = true
                onDismiss()
            })
        }
    }

    if (showAlertSent) {
        AlertSentDialog(onDismiss)
    } else {
        AlertDialog(
            onDismissRequest = { alertViewModel.setCanceled(true); onDismiss() },
            title = { R.string.alert_sending_title },
            text = { R.string.alert_sending_text.toString() + " $countdown segundos." },
            confirmButton = {
                Button(
                    onClick = {
                        alertViewModel.setCanceled(true)
                        onDismiss()
                    }
                ) {
                    R.string.cancel
                }
            }
        )
    }
}

@Composable
fun AlertButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(64.dp),
            tint = color
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
        )
    }
}

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
fun AlertSentDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { R.string.alert_sent_title },
        text = { R.string.alert_sent_text.toString() },
        confirmButton = {
            Button(onClick = onDismiss) {
                R.string.close
            }
        },
        dismissButton = { },
    )
}