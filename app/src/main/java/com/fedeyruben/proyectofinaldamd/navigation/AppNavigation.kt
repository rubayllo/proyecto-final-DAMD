package com.fedeyruben.proyectofinaldamd.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.launchScreen.LaunchScreenInit
import com.fedeyruben.proyectofinaldamd.maps.MapScreenInit
import com.fedeyruben.proyectofinaldamd.register.registerScreen.RegisterScreenInit
import com.fedeyruben.proyectofinaldamd.register.registerVerifyScreen.RegisterVerifyScreenInit


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreensRoutes.RegisterScreen.route
    ) {
        composable(AppScreensRoutes.LaunchScreen.route) {
            LaunchScreenInit(navController)
            Log.d("Flujo: Navigation", "LaunchScreenInit")
        }
 //       composable(AppScreensRoutes.BottomNavigate.route) {
  //          AppContent(navController)
 //           Log.d("Flujo: Navigation", "LaunchScreenInit")
  //      }
//        composable(AppScreensRoutes.LoginScreen.route) {
//            LoginScreenInit(navController)
//            Log.d("Flujo: Navigation", "LoginScreenInit")
//        }
        composable(AppScreensRoutes.RegisterScreen.route) {
            RegisterScreenInit(navController)
        }

        composable(AppScreensRoutes.MapScreen.route) {
            MapScreenInit(navController)
            Log.d("Flujo: Navigation", "MapScreenInit")
        }

        composable(AppScreensRoutes.RegisterVerifyScreen.route) {
            RegisterVerifyScreenInit(navController)
        }


    }

}



