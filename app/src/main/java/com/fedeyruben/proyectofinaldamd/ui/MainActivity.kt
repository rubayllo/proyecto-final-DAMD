package com.fedeyruben.proyectofinaldamd.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.fedeyruben.proyectofinaldamd.data.permissions.PermissionUtils
import com.fedeyruben.proyectofinaldamd.data.permissions.ShowPermissionExplanationDialog
import com.fedeyruben.proyectofinaldamd.ui.friendsScreen.FriendsViewModel
import com.fedeyruben.proyectofinaldamd.ui.navigation.AppNavigation
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.viewModel.RegisterViewModel
import com.fedeyruben.proyectofinaldamd.ui.settingsScreen.SettingsViewModel
import com.fedeyruben.proyectofinaldamd.ui.theme.ProyectoFinalDAMDTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val friendsViewModel : FriendsViewModel by viewModels()
    private val settingsViewModel : SettingsViewModel by viewModels()
    private val registerViewModel : RegisterViewModel by viewModels()

    /** Acceso a la agenda telefonica */
    private val pickContactResultLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
        uri?.let {
            Log.i("ok","${it.userInfo}")
            friendsViewModel.readContactData(this, uri)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalDAMDTheme {
                val isUserRegistered by registerViewModel.isUserRegistered.observeAsState(initial = false)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    isUserRegistered?.let { registered ->
                        AppNavigation(
                            pickContactResultLauncher,
                            friendsViewModel,
                            settingsViewModel,
                            registerViewModel,
                            this@MainActivity,
                            registered
                        )
                        val permissionState =
                            rememberMultiplePermissionsState(permissions = PermissionUtils.permissionsList)

                        if (!permissionState.allPermissionsGranted) {
                            ShowPermissionExplanationDialog()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requestMultiplePermissionsLauncher.launch(PermissionUtils.permissionsArray)
    }

    private var isPermissionsDialog = false
    private var isPermissionsGranted = false

    private fun showBluetoothDialog() {
        if (!isPermissionsGranted) {
            requestMultiplePermissionsLauncher.launch(PermissionUtils.permissionsArray)
//        } else if (isPermissionsGranted && !bluetoothAdapter.isEnabled) {
        } else if (isPermissionsGranted) {
            if (!isPermissionsDialog) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                startBluetoothIntentForResult.launch(enableBtIntent)
                isPermissionsDialog = true
            }
        } else if (!isPermissionsGranted) {
            if (!isPermissionsDialog) {
                requestMultiplePermissionsLauncher.launch(PermissionUtils.permissionsArray)
            }
        }
    }

//    private val startBluetoothIntentForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            isPermissionsDialog = false
//            if (result.resultCode != Activity.RESULT_OK) {
//                // Bluetooth no conectado
//                showBluetoothDialog()
//            }
//        }


    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.all { isGranted ->
                isGranted.value
            }
            if (allPermissionsGranted) {
                isPermissionsGranted = true
            }
            showBluetoothDialog()
        }
}

