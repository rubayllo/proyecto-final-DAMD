package com.fedeyruben.proyectofinaldamd.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
<<<<<<< HEAD
import com.fedeyruben.proyectofinaldamd.launchScreen.LaunchScreenInit
import com.fedeyruben.proyectofinaldamd.maps.MapScreenInit
import com.fedeyruben.proyectofinaldamd.registerScreen.RegisterScreenInit
=======
import com.fedeyruben.proyectofinaldamd.register.registerScreen.RegisterScreenInit
import com.fedeyruben.proyectofinaldamd.register.registerVerifyScreen.RegisterVerifyScreenInit
>>>>>>> origin/ruben

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
        }
<<<<<<< HEAD

        composable(AppScreensRoutes.MapScreen.route) {
            MapScreenInit(navController)
            Log.d("Flujo: Navigation", "MapScreenInit")
        }
=======
        composable(AppScreensRoutes.RegisterVerifyScreen.route) {
            RegisterVerifyScreenInit(navController)
        }

>>>>>>> origin/ruben
    }

}



