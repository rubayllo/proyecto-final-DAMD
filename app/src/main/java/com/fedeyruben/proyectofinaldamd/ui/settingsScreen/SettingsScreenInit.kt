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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation.bottomBarHeight

@Composable
fun SettingsScreenInit(settingsViewModel: SettingsViewModel) {

    val settingsList = remember {
        mutableStateListOf(
            "Configuración personal:" to listOf(
                SettingsItem("Cambiar Fotografía", null, Icons.Default.PhotoCamera, null),
                SettingsItem("Cambiar Nombre de Usuario", null, Icons.Default.AccountCircle, null)
            ),
            "Configura tus guardianes:" to listOf(
                SettingsItem("Modo Siempre en Alerta", null, Icons.Default.Warning, true),
                SettingsItem("Nivel de Alerta Bajo","LowGuardian", Icons.Default.AddAlert, null),
                SettingsItem("Nivel de Alerta Medio","MidGuardian", Icons.Default.AddAlert, null),
                SettingsItem("Nivel de Alerta Alto", "HighGuardian", Icons.Default.AddAlert, null),
                SettingsItem("Nivel de Alerta Máximo","MaxGuardian", Icons.Default.AddAlert, null)
            ),
            "Acepta ser protector de:" to listOf(
                SettingsItem("Solicitud de Protección", "ProtectTo", Icons.Default.PersonAdd, null)
            )
        )
    }

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
                SettingsOption(item, settingsViewModel)
                Divider()
            }
        }
    }
}

@Composable
fun SettingsOption(item: SettingsItem, settingsViewModel: SettingsViewModel) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                dropdownExpanded = !dropdownExpanded
            })
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

        var additionalSwitchState by remember { mutableStateOf(false) }
        if (item.switchState != null) {
            Switch(
                checked = additionalSwitchState,
                onCheckedChange = { newState ->
                    additionalSwitchState = newState
                }
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
        DropDown(item.type, settingsViewModel)
    }
}

@Composable
private fun DropDown(type: String?, settingsViewModel: SettingsViewModel) {

    val amigos by settingsViewModel.userGuardiansContactsList.collectAsState()
    val guardianAlertLevelList by settingsViewModel.guardianAlertLevelList.collectAsState()

    // Lista de contactos que te han solicitado protección (simulada)
    val solicitudProteccionList = listOf(
        "Proteger contacto 1",
        "Proteger contacto 2",
        "Proteger contacto 3"
    )

//    val lowGuardianList = listOf(
//        "Contacto 1 nivel bajo",
//        "Contacto 2 nivel bajo",
//        "Contacto 3 nivel bajo"
//    )
    val midGuardianList = listOf(
        "Contacto 1 nivel medio",
        "Contacto 2 nivel medio",
        "Contacto 3 nivel medio"
    )
    val highGuardianList = listOf(
        "Contacto 1 nivel alto",
        "Contacto 2 nivel alto",
        "Contacto 3 nivel alto"
    )
    val maxGuardianList = listOf(
        "Contacto 1 nivel máximo",
        "Contacto 2 nivel máximo",
        "Contacto 3 nivel máximo"
    )

    when (type) {
        "ProtectTo" -> {
            solicitudProteccionList.forEach { contact ->
                ExpandMenu(contact)
            }
        }
        "LowGuardian" -> {
//            lowGuardianList.forEach { contact ->
//                ExpandMenu(contact)
//            }
            guardianAlertLevelList.forEach { contact ->
                if(contact.low){
                    ExpandMenu(contact.userGuardianId)
                }
            }
        }
        "MidGuardian" -> {
            midGuardianList.forEach { contact ->
                ExpandMenu(contact)
            }
        }
        "HighGuardian" -> {
            highGuardianList.forEach { contact ->
                ExpandMenu(contact)
            }
        }
        "MaxGuardian" -> {
            maxGuardianList.forEach { contact ->
                ExpandMenu(contact)
            }
        }

    }

}

@Composable
private fun ExpandMenu(contact: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(start = 56.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(contact)
        Log.d("SettingsScreen", "Contacto: $contact")
    }
}

data class SettingsItem(
    val title: String,
    val type: String? = null,
    val icon: ImageVector,
    var switchState: Boolean?
)




