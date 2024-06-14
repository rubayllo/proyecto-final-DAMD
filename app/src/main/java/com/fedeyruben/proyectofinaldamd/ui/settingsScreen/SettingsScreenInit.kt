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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.data.room.model.UserProtected
import com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation.bottomBarHeight
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertCriticalColor
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertHighColor
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertLowColor
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertMidColor

@Composable
fun SettingsScreenInit(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    settingsViewModel.iniciarFirestoreRecogerProtegidos(context)
    val settingsList = remember {
        mutableStateListOf(
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
            "Configura tus protegidos:" to listOf(
                SettingsItem(
                    "Listado de Protegidos",
                    "ListProtect",
                    Icons.Default.PersonAdd,
                    null,
                    null
                ),
                SettingsItem(
                    "Solicitudes de Protección",
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
            val icono =
                if (!dropdownExpanded) {
                    Icons.Default.ArrowDropDown
                } else {
                    Icons.Default.ArrowDropUp
                }
            Icon(
                imageVector = icono,
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
    val protectedGuardiansContactsList by settingsViewModel.protectedGuardiansContactsList.collectAsState()

    val context = LocalContext.current

    val (protected, requests) = settingsViewModel.countProtectedAndRequests(protectedGuardiansContactsList)

    when (type) {

        "ProtectTo" -> {
            if (requests == 0) {
                Text(
                    text = "No tienes solicitudes de protección",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 26.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                )
            } else {
                protectedGuardiansContactsList.forEach { amigo ->
                    if (!amigo.isProtected) {
                        val amigoName =
                            settingsViewModel.recuperarNombreTelefono(
                                context,
                                amigo.userPhoneProtected
                            )
                        ExpandMenuProtect(
                            amigoName,
                            amigo.userPhoneProtected,
                            settingsViewModel,
                            false
                        )
                    }
                }
            }
        }

        "ListProtect" -> {
            if (protected == 0) {
                Text(
                    text = "No tienes protegidos",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 26.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)

                )
            } else {
                protectedGuardiansContactsList.forEach { amigo ->
                    if (amigo.isProtected) {
                        val amigoName =
                            settingsViewModel.recuperarNombreTelefono(
                                context,
                                amigo.userPhoneProtected
                            )
                        ExpandMenuProtect(
                            amigoName,
                            amigo.userPhoneProtected,
                            settingsViewModel,
                            true
                        )
                    }
                }
            }
        }

        "LowGuardian" -> {
            val noTrueLow = hasNoTrue(guardianAlertLevelList) { it.low }
            if (noTrueLow) {
                Text(
                    text = "No tienes guardianes en este nivel",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 26.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                )
            } else {
                guardianAlertLevelList.forEach { contact ->
                    if (contact.low) {
                        ViewFriendGuardian(amigos, contact, 0, settingsViewModel)
                    }
                }
            }
        }

        "MidGuardian" -> {
            val noTrueMedium = hasNoTrue(guardianAlertLevelList) { it.medium }
            if (noTrueMedium) {
                Text(
                    text = "No tienes guardianes en este nivel",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 26.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)                )
            } else {
                guardianAlertLevelList.forEach { contact ->
                    if (contact.medium) {
                        ViewFriendGuardian(amigos, contact, 1, settingsViewModel)
                    }
                }
            }
        }

        "HighGuardian" -> {
            val noTrueHigh = hasNoTrue(guardianAlertLevelList) { it.high }
            if (noTrueHigh) {
                Text(
                    text = "No tienes guardianes en este nivel",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 26.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)                )
            } else {
                guardianAlertLevelList.forEach { contact ->
                    if (contact.high) {
                        ViewFriendGuardian(amigos, contact, 2, settingsViewModel)
                    }
                }
            }
        }

        "MaxGuardian" -> {
            val noTrueCritical = hasNoTrue(guardianAlertLevelList) { it.critical }
            if (noTrueCritical) {
                Text(
                    text = "No tienes guardianes en este nivel",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 26.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)                )
            } else {
                guardianAlertLevelList.forEach { contact ->
                    if (contact.critical) {
                        ViewFriendGuardian(amigos, contact, 3, settingsViewModel)
                    }
                }
            }
        }
    }
}


// Función auxiliar para verificar si ningún elemento cumple con el predicado
private fun <T> hasNoTrue(list: List<T>, predicate: (T) -> Boolean): Boolean {
    for (item in list) {
        if (predicate(item)) {
            return false
        }
    }
    return true
}


@Composable
fun ExpandMenuProtect(
    amigoName: String,
    amigoPhoneNumber: String,
    settingsViewModel: SettingsViewModel,
    isProtected: Boolean
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(start = 26.dp, end = 16.dp, top = 2.dp, bottom = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically // Alineación vertical al centro

        ) {
            Text(
                text = amigoName,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start, // Cambiado a Start
                modifier = Modifier.weight(1f) // Añadir peso para ocupar el espacio disponible
            )
            TextButton(
                onClick = {
                    // Acción para aceptar o quitar la protección
                    settingsViewModel.updateIsGuardianRegister(amigoPhoneNumber, !isProtected, context)
                }
            ) {
                Text(
                    text = if (isProtected) "Quitar" else "Aceptar",
                    color = if (isProtected) AlertHighColor else AlertLowColor
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
            ExpandMenuLevelGuardianAlert(
                amigo.guardianName,
                guardianAlertLevel.userGuardianId,
                levelGuardianAlert,
                settingsViewModel
            )
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
                    settingsViewModel.updateGuardianAlertLevel(
                        userGuardianId!!,
                        levelGuardianAlert,
                        false
                    )
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
                    settingsViewModel.updateGuardianAlertLevel(
                        userGuardianId!!,
                        levelGuardianAlert,
                        false
                    )
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




