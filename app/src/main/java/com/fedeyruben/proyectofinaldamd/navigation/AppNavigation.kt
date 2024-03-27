package com.fedeyruben.proyectofinaldamd.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.launchScreen.LaunchScreenInit
import com.fedeyruben.proyectofinaldamd.maps.MapScreenInit
import com.fedeyruben.proyectofinaldamd.registerScreen.RegisterScreenInit

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
//        composable(AppScreensRoutes.LoginScreen.route) {
//            LoginScreenInit(navController)
//            Log.d("Flujo: Navigation", "LoginScreenInit")
//        }
        composable(AppScreensRoutes.RegisterScreen.route) {
            RegisterScreenInit(navController)
            Log.d("Flujo: Navigation", "RegisterScreenInit")
        }

        composable(AppScreensRoutes.MapScreen.route) {
            MapScreenInit(navController)
            Log.d("Flujo: Navigation", "MapScreenInit")
        }
    }

}

