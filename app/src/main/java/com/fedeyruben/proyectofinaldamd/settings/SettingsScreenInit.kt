package com.fedeyruben.proyectofinaldamd.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fence
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HistoryToggleOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsScreenInit() {
    val settingsList = listOf(
        "Location Privacy" to listOf(
            SettingsItem("Share Location", Icons.Default.LocationOn, true),
            SettingsItem("Update Frequency", Icons.Default.Update, null),
            SettingsItem("Geofencing", Icons.Default.Fence, null)
        ),
        "Alerts" to listOf(
            SettingsItem("Emergency Alerts", Icons.Default.Warning, true),
            SettingsItem("Alert Type", Icons.Default.AddAlert, null),
            SettingsItem("Alert Level", Icons.Default.ErrorOutline, null)
        ),
        "Modes" to listOf(
            SettingsItem("Night Mode", Icons.Default.NightsStay, true),
            SettingsItem("Silent Mode", Icons.Default.DoNotDisturbOn, true)
        ),
        "Interface Customization" to listOf(
            SettingsItem("App Theme", Icons.Default.ColorLens, null),
            SettingsItem("Text Size", Icons.Default.TextFields, null)
        ),
        "Connections & Permissions" to listOf(
            SettingsItem("Manage Contacts", Icons.Default.PeopleAlt, null),
            SettingsItem("App Permissions", Icons.Default.SettingsApplications, null)
        ),
        "Advanced Notifications" to listOf(
            SettingsItem("Custom Notification Sound", Icons.Default.MusicNote, null),
            SettingsItem("Location-Based Alerts", Icons.Default.MyLocation, null)
        ),
        "Other Settings" to listOf(
            SettingsItem("Password", Icons.Default.VpnKey, null)
        )
    )
    // Ajusta esto a la altura de tu BottomNavigation
    val bottomBarHeight = 80.dp
    LazyColumn(
        contentPadding = PaddingValues(bottom = bottomBarHeight)
    ) {
        settingsList.forEach { (category, items) ->
            stickyHeader {
                Text(
                    text = category,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.background)
                )
            }
            items(items) { item ->
                SettingsOption(item)
                Divider()
            }
        }
    }
}

@Composable
fun SettingsOption(item: SettingsItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
}

data class SettingsItem(
    val title: String,
    val icon: ImageVector,
    val switchState: Boolean?
)
