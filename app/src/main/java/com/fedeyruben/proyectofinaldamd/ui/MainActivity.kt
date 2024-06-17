package com.fedeyruben.proyectofinaldamd.ui


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.fedeyruben.proyectofinaldamd.data.permissions.PermissionUtils
import com.fedeyruben.proyectofinaldamd.data.permissions.PermissionUtils.permissionsList
import com.fedeyruben.proyectofinaldamd.data.permissions.ShowPermissionExplanationDialog
import com.fedeyruben.proyectofinaldamd.ui.alertScreen.AlertFriendDialog
import com.fedeyruben.proyectofinaldamd.ui.alertScreen.AlertViewModel
import com.fedeyruben.proyectofinaldamd.ui.friendsScreen.FriendsViewModel
import com.fedeyruben.proyectofinaldamd.ui.mapsScreen.MapViewModel
import com.fedeyruben.proyectofinaldamd.ui.navigation.AppNavigation
import com.fedeyruben.proyectofinaldamd.ui.navigation.AppScreensRoutes
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.viewModel.RegisterViewModel
import com.fedeyruben.proyectofinaldamd.ui.settingsScreen.SettingsViewModel
import com.fedeyruben.proyectofinaldamd.ui.theme.ProyectoFinalDAMDTheme
import com.fedeyruben.proyectofinaldamd.utils.LocationUpdateService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val friendsViewModel: FriendsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val alertViewModel: AlertViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()

    private val pickContactResultLauncher =
        registerForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
            uri?.let {
                Log.i("ok", "${it.userInfo}")
                friendsViewModel.readContactData(this, uri)
            }
        }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalDAMDTheme {
                val isUserRegistered by registerViewModel.isUserRegistered.observeAsState(initial = false)
                val navController = rememberNavController()
                var showDialog by rememberSaveable { mutableStateOf(false) }
                var friendName by rememberSaveable { mutableStateOf("") }
                val friendAlertName by mapViewModel.friendAlertName.observeAsState()

                LaunchedEffect(mapViewModel.friendAlertLocation) {
                    mapViewModel.friendAlertLocation.observe(this@MainActivity) { alertLocation ->
                        if (alertLocation != null) {
                            friendName = friendAlertName?:"Amigo"
                            showDialog = true
                        }
                    }
                }

                if (showDialog) {
                    AlertFriendDialog(
                        friendName = friendName,
                        onDismiss = { showDialog = false },
                        onNavigate = {
                            showDialog = false
                            navController.navigate(AppScreensRoutes.MapScreen.route)
                        }
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    isUserRegistered?.let { registered ->
                        AppNavigation(
                            navController,
                            pickContactResultLauncher,
                            friendsViewModel,
                            settingsViewModel,
                            registerViewModel,
                            this@MainActivity,
                            registered,
                            alertViewModel,
                            mapViewModel
                        )
                        val permissionState =
                            rememberMultiplePermissionsState(permissions = permissionsList)
                        var isDialogShown by rememberSaveable { mutableStateOf(false) }
                        if (!permissionState.allPermissionsGranted && registered) {
                            if (!isDialogShown) {
                                requestMultiplePermissionsLauncher.launch(PermissionUtils.permissionsArray)
                                isDialogShown = true
                            } else if (!permissionState.shouldShowRationale) {
                                ShowPermissionExplanationDialog()
                            }
                        } else if (permissionState.allPermissionsGranted && !isDialogShown) {
                            isDialogShown = true
                            startLocationService()
                        }
                    }
                }
            }
        }
    }

    private fun startLocationService() {
        val intent = Intent(this, LocationUpdateService::class.java)
        startService(intent)
    }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("Permission", "${it.key} = ${it.value}")
            }
            if (permissions.entries.all { it.value }) {
                startLocationService()
            }
        }
}

