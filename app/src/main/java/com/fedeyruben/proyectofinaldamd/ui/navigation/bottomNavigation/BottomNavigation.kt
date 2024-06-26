package com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation

import android.annotation.SuppressLint
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CrisisAlert
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.ui.friendsScreen.FriendsViewModel
import com.fedeyruben.proyectofinaldamd.ui.alertScreen.AlertScreenInit
import com.fedeyruben.proyectofinaldamd.ui.alertScreen.AlertViewModel
import com.fedeyruben.proyectofinaldamd.ui.friendsScreen.FriendsScreenInit
import com.fedeyruben.proyectofinaldamd.ui.mapsScreen.MapScreenInit
import com.fedeyruben.proyectofinaldamd.ui.mapsScreen.MapViewModel
import com.fedeyruben.proyectofinaldamd.ui.settingsScreen.SettingsScreenInit
import com.fedeyruben.proyectofinaldamd.ui.settingsScreen.SettingsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

// Ajusta esto a la altura de tu BottomNavigation
val bottomBarHeight = 120.dp

@OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreenInit(pickContactResultLauncher: ActivityResultLauncher<Void?>, friendsViewModel: FriendsViewModel, settingsViewModel: SettingsViewModel, alertViewModel: AlertViewModel,mapViewModel: MapViewModel) {

    /** Permisos agenda luego gestionar que si cancela dos veces lo mande a las settings*/
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS
        )
    )

    // Definir los ítems de la barra de navegación
    val alertTab = TabBarItem(
        title = stringResource(id = R.string.alert),
        selectedIcon = Icons.Filled.CrisisAlert,
        unselectedIcon = Icons.Outlined.CrisisAlert
    )
    val friendsTab = TabBarItem(
        title = stringResource(id = R.string.guards),
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    )
    val mapsTab = TabBarItem(
        title = stringResource(id = R.string.maps),
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )
    val settingsTab = TabBarItem(
        title = stringResource(id = R.string.settings),
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    val tabBarItems = listOf(alertTab, friendsTab, mapsTab, settingsTab)

    val navController = rememberNavController()

    // Estado para mostrar el FloatingActionButton en la pantalla de amigos
    val showFab = remember { mutableStateOf(false) }

    // Escucha cambios en la pantalla actual y actualiza showFab
    LaunchedEffect(navController.currentBackStackEntry) {
        showFab.value = (navController.currentDestination?.route == "Friends")
    }

    Scaffold(
        bottomBar = { TabView(tabBarItems, navController) },
        floatingActionButton = {
            if (showFab.value) {
                FloatingActionButton(onClick = {
                    if (!permissionsState.allPermissionsGranted) {
                        permissionsState.launchMultiplePermissionRequest()
                    } else {
                        pickContactResultLauncher.launch(null)
                    }
                    }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_friend))
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = alertTab.title) {
            composable(alertTab.title) {
                showFab.value = false
                AlertScreenInit(alertViewModel)
            }
            composable(friendsTab.title) {
                showFab.value = true
                FriendsScreenInit(friendsViewModel)
            }
            composable(mapsTab.title) {
                showFab.value = false
                MapScreenInit(mapViewModel)
            }
            composable(settingsTab.title) {
                showFab.value = false
                SettingsScreenInit(settingsViewModel)
            }
        }
    }
}


@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title
                    )
                },
                label = { Text(tabBarItem.title) })
        }
    }
}

@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String
) {
    Icon(
        modifier = Modifier.size(24.dp),
        imageVector = if (isSelected) {
            selectedIcon
        } else {
            unselectedIcon
        },
        contentDescription = title
    )
}

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
