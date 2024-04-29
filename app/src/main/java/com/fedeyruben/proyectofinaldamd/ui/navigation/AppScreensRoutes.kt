package com.fedeyruben.proyectofinaldamd.ui.navigation

sealed class AppScreensRoutes(val route: String) {
    object LaunchScreen : AppScreensRoutes("launch_screen")
    object RegisterScreen : AppScreensRoutes("register_screen")
    object MapScreen : AppScreensRoutes("map_screen")
    object HomeScreen : AppScreensRoutes("home_screen")

}