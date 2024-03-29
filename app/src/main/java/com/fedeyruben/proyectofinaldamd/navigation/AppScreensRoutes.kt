package com.fedeyruben.proyectofinaldamd.navigation

sealed class AppScreensRoutes (val route: String){
    object LaunchScreen : AppScreensRoutes("launch_screen")
    object LoginScreen : AppScreensRoutes("login_screen")
    object RegisterScreen : AppScreensRoutes("register_screen")
<<<<<<< HEAD
    object MapScreen : AppScreensRoutes("map_screen")
=======
    object RegisterVerifyScreen : AppScreensRoutes("register_verify")
>>>>>>> origin/ruben
}