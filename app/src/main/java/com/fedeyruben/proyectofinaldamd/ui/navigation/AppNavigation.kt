package com.fedeyruben.proyectofinaldamd.ui.navigation

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.ui.friendsScreen.FriendsViewModel
import com.fedeyruben.proyectofinaldamd.ui.MainActivity
import com.fedeyruben.proyectofinaldamd.ui.launchScreen.LaunchScreenInit
import com.fedeyruben.proyectofinaldamd.ui.mapsScreen.MapScreenInit
import com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation.HomeScreenInit
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.registerScreen.RegisterScreenInit
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.viewModel.RegisterViewModel
import com.fedeyruben.proyectofinaldamd.ui.settingsScreen.SettingsViewModel


@Composable
fun AppNavigation(
    pickContactResultLauncher: ActivityResultLauncher<Void?>,
    friendsViewModel: FriendsViewModel,
    settingsViewModel: SettingsViewModel,
    registerViewModel: RegisterViewModel,
    activity: MainActivity
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreensRoutes.LaunchScreen.route

    ) {
        composable(AppScreensRoutes.LaunchScreen.route) {
            LaunchScreenInit(navController)
        }

        composable(AppScreensRoutes.RegisterScreen.route) {
            RegisterScreenInit(navController, activity, registerViewModel)
        }

        composable(AppScreensRoutes.MapScreen.route) {
            MapScreenInit()
            Log.d("Flujo: Navigation", "MapScreenInit")
        }

        composable(AppScreensRoutes.HomeScreen.route) {
            HomeScreenInit(
                pickContactResultLauncher,
                friendsViewModel,
                settingsViewModel
            ) // Esta es tu pantalla con BottomNavigation y su propia NavHost
        }
    }
}



