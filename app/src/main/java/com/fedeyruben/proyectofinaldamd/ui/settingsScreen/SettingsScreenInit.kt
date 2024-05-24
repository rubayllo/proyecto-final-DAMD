package com.fedeyruben.proyectofinaldamd.ui.settingsScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation.bottomBarHeight
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertCriticalColor
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertHighColor
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertLowColor
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertMidColor

@Composable
fun SettingsScreenInit(settingsViewModel: SettingsViewModel) {

    val settingsList = remember {
        mutableStateListOf(
            "Configuración personal:" to listOf(
                SettingsItem("Cambiar Fotografía", null, Icons.Default.PhotoCamera, null, null),
                SettingsItem(
                    "Cambiar Nombre de Usuario",
                    null,
                    Icons.Default.AccountCircle,
                    null,
                    null
                )
            ),
            "Configura tus guardianes:" to listOf(
                SettingsItem("Modo Siempre en Alerta", null, Icons.Default.Warning, null, true),
                SettingsItem(
                    "Nivel de Alerta Bajo",
                    "LowGuardian",
                    Icons.Default.AddAlert,
                    AlertLowColor,
                    null
                ),
                SettingsItem(
                    "Nivel de Alerta Medio",
                    "MidGuardian",
                    Icons.Default.AddAlert,
                    AlertMidColor,
                    null
                ),
                SettingsItem(
                    "Nivel de Alerta Alto",
                    "HighGuardian",
                    Icons.Default.AddAlert,
                    AlertHighColor,
                    null
                ),
                SettingsItem(
                    "Nivel de Alerta Máximo",
                    "MaxGuardian",
                    Icons.Default.AddAlert,
                    AlertCriticalColor,
                    null
                )
            ),
            "Acepta ser protector de:" to listOf(
                SettingsItem(
                    "Solicitud de Protección",
                    "ProtectTo",
                    Icons.Default.PersonAdd,
                    null,
                    null
                )
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
            modifier = Modifier.size(24.dp),
            tint = item.iconColor
                ?: Color.Unspecified // Usa el color especificado o un color por defecto
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



    when (type) {
        "ProtectTo" -> {
            solicitudProteccionList.forEach { contact ->
                ExpandMenuProtectTo(contact, settingsViewModel)
            }
        }

        "LowGuardian" -> {
            guardianAlertLevelList.forEach { contact ->
                if (contact.low) {
                    ViewFriendGuardian(amigos, contact, 0, settingsViewModel)
                }
            }
        }

        "MidGuardian" -> {
            guardianAlertLevelList.forEach { contact ->
                if (contact.medium) {
                    ViewFriendGuardian(amigos, contact, 1, settingsViewModel)
                }
            }
        }

        "HighGuardian" -> {
            guardianAlertLevelList.forEach { contact ->
                if (contact.high) {
                    ViewFriendGuardian(amigos, contact,2, settingsViewModel)
                }
            }
        }

        "MaxGuardian" -> {
            guardianAlertLevelList.forEach { contact ->
                if (contact.critical) {
                    ViewFriendGuardian(amigos, contact,3, settingsViewModel)
                }
            }
        }

    }
}

@Composable
fun ExpandMenuProtectTo(contact: String, settingsViewModel: SettingsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(start = 26.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Text(
            text = contact,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = {
                    // Acción para aceptar la solicitud de protección
                }
            ) {
                Text(
                    text = "Aceptar",
                    color = AlertLowColor
                )
            }
            TextButton(
                onClick = {
                    // Acción para rechazar la solicitud de protección
                }
            ) {
                Text(
                    text = "Rechazar",
                    color = AlertHighColor
                )
            }
        }
    }
}


@Composable
private fun ViewFriendGuardian(
    amigos: List<UserGuardiansContacts>,
    guardianAlertLevel: GuardianAlertLevel,
    levelGuardianAlert: Int,
    settingsViewModel: SettingsViewModel
) {
    amigos.forEach { amigo ->
        if (amigo.guardianPhoneNumber == guardianAlertLevel.userGuardianId) {
            ExpandMenuLevelGuardianAlert(amigo.guardianName, guardianAlertLevel.userGuardianId, levelGuardianAlert, settingsViewModel)
        }
    }
}

@Composable
private fun ExpandMenuLevelGuardianAlert(
    contact: String,
    userGuardianId: String?,
    levelGuardianAlert: Int,
    settingsViewModel: SettingsViewModel
) {
    if (contact.split(" ").size > 1) {
        val name = contact.split(" ")[0]
        val surname = contact.split(" ")[1]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {})
                .padding(start = 26.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "- $name $surname",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    // Acción para quitar el contacto
                    settingsViewModel.updateGuardianAlertLevel(userGuardianId!!, levelGuardianAlert, false)
                }
            ) {
                Text(
                    text = "Quitar",
                    color = Color.Red
                )
            }
            Log.d("SettingsScreen", "Contacto: $contact")
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {})
                .padding(start = 26.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "- $contact",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    // Acción para quitar el contacto
                    settingsViewModel.updateGuardianAlertLevel(userGuardianId!!, levelGuardianAlert, false)
                }
            ) {
                Text(
                    text = "Quitar",
                    color = Color.Red
                )
            }
            Log.d("SettingsScreen", "Contacto: $contact")
        }
    }
}

data class SettingsItem(
    val title: String,
    val type: String? = null,
    val icon: ImageVector,
    val iconColor: Color? = null,
    var switchState: Boolean?
)




