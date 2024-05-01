package com.fedeyruben.proyectofinaldamd.ui.settingsScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreenInit() {

    val settingsList = remember {
        mutableStateListOf(
            "Configuración personal:" to listOf(
                SettingsItem("Cambiar Fotografía", Icons.Default.PhotoCamera, null),
                SettingsItem("Cambiar Nombre de Usuario", Icons.Default.AccountCircle, null)
            ),
            "Configura tus guardianes:" to listOf(
                SettingsItem("Modo Siempre en Alerta", Icons.Default.Warning, true),
                SettingsItem("Nivel de Alerta Bajo", Icons.Default.AddAlert, null),
                SettingsItem("Nivel de Alerta Medio", Icons.Default.AddAlert, null),
                SettingsItem("Nivel de Alerta Alto", Icons.Default.AddAlert, null),
                SettingsItem("Nivel de Alerta Máximo", Icons.Default.AddAlert, null)
            ),
            "Acepta ser protector de:" to listOf(
                SettingsItem("Solicitud de Protección", Icons.Default.PersonAdd, null)
            )
        )
    }

    // Ajusta esto a la altura de tu BottomNavigation
    val bottomBarHeight = 120.dp

    LazyColumn(
        contentPadding = PaddingValues(bottom = bottomBarHeight)
    ) {
        items(settingsList) { (category, items) ->
            Text(
                text = category,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 46.dp, start = 16.dp, end = 16.dp)
                    .background(MaterialTheme.colorScheme.background)
            )

            items.forEach { item ->
                SettingsOption(item)
                Divider()
            }
        }
    }
}


@Composable
fun SettingsOption(item: SettingsItem) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                dropdownExpanded = !dropdownExpanded
            }) // Agrega la capacidad de hacer clic
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.weight(1f))
        if (item.switchState != null) {
            Switch(
                checked = item.switchState,
                onCheckedChange = { /* TODO: Manejar cambio de estado */ }
            )
        } else {
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = "Navigate",
                modifier = Modifier.size(24.dp)
            )
        }
    }
    if (dropdownExpanded) {
        AddProtegido(item.title)
    }
}

@Composable
private fun AddProtegido(title: String) {
    // Lista de contactos que te han solicitado protección (simulada)
    val solicitudProteccionList = listOf(
        "Contacto 1",
        "Contacto 2",
        "Contacto 3"
    )

    if (true) {

        solicitudProteccionList.forEach { contact ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = {})
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(contact)
                Log.d("SettingsScreen", "Contacto: $contact")
            }
        }
    }

}

data class SettingsItem(
    val title: String,
    val icon: ImageVector,
    val switchState: Boolean?
)



