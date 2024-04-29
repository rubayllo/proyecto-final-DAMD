package com.fedeyruben.proyectofinaldamd.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation.HomeScreenInit
import com.fedeyruben.proyectofinaldamd.ui.launchScreen.LaunchScreenInit
import com.fedeyruben.proyectofinaldamd.ui.mapsScreen.MapScreenInit
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.registerScreen.RegisterScreenInit


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreensRoutes.LaunchScreen.route

    ) {
        composable(AppScreensRoutes.LaunchScreen.route) {
            LaunchScreenInit(navController)
            Log.d("Flujo: Navigation", "LaunchScreenInit")
        }

        composable(AppScreensRoutes.RegisterScreen.route) {
            RegisterScreenInit(navController)
        }

        composable(AppScreensRoutes.MapScreen.route) {
            MapScreenInit()
            Log.d("Flujo: Navigation", "MapScreenInit")
        }

        composable(AppScreensRoutes.HomeScreen.route) {
            HomeScreenInit() // Esta es tu pantalla con BottomNavigation y su propia NavHost
        }
    }
}



