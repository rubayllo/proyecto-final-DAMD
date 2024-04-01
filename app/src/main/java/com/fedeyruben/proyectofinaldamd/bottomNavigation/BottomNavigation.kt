package com.fedeyruben.proyectofinaldamd.bottomNavigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CrisisAlert
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.alertScreen.AlertScreenInit
import com.fedeyruben.proyectofinaldamd.friends.FriendsScreenInit
import com.fedeyruben.proyectofinaldamd.maps.MapScreenInit


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreenInit() {
    // Definir los ítems de la barra de navegación
    val alertTab = TabBarItem(
        title = "Alert",
        selectedIcon = Icons.Filled.CrisisAlert,
        unselectedIcon = Icons.Outlined.CrisisAlert
    )
    val friendsTab = TabBarItem(
        title = "Friends",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    )
    val mapsTab = TabBarItem(
        title = "Maps",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn
    )
    val settingsTab = TabBarItem(
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    val tabBarItems = listOf(alertTab, friendsTab, mapsTab, settingsTab)

    val navController = rememberNavController()
    Scaffold(bottomBar = { TabView(tabBarItems, navController) }) {
        NavHost(navController = navController, startDestination = alertTab.title) {
            composable(alertTab.title) {
               AlertScreenInit()
            }
            composable(friendsTab.title) {
                FriendsScreenInit()
            }
            composable(mapsTab.title) {
                MapScreenInit()
            }
            composable(settingsTab.title) {
             //   SettingsScreenInit()
            }
        }
    }
}



@Composable
fun ButtonWithIconAndText(icon: ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
    var selectedTabIndex by rememberSaveable {
        mutableStateOf(0)
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
        imageVector = if (isSelected) {
            selectedIcon
        } else {
            unselectedIcon
        },
        contentDescription = title
    )
}


@Composable
fun MoreView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Text("Amigo 1")
        Text("Amigo 2")
        Text("Amigo 3")
        Text("Amigo 4")
        Text("Amigo 5")
    }
}


data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
