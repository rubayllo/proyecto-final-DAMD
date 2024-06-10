package com.fedeyruben.proyectofinaldamd.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fedeyruben.proyectofinaldamd.ui.friendsScreen.FriendsViewModel
import com.fedeyruben.proyectofinaldamd.ui.navigation.AppNavigation
import com.fedeyruben.proyectofinaldamd.ui.registerScreen.viewModel.RegisterViewModel
import com.fedeyruben.proyectofinaldamd.ui.settingsScreen.SettingsViewModel
import com.fedeyruben.proyectofinaldamd.ui.theme.ProyectoFinalDAMDTheme
import com.fedeyruben.proyectofinaldamd.utils.LocationUpdateService
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val friendsViewModel : FriendsViewModel by viewModels()
    private val settingsViewModel : SettingsViewModel by viewModels()
    private val registerViewModel : RegisterViewModel by viewModels()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    /** Acceso a la agenda telefonica */
    private val pickContactResultLauncher = registerForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
        uri?.let {
            Log.i("ok","${it.userInfo}")
            friendsViewModel.readContactData(this, uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoFinalDAMDTheme {
                val isUserRegistered by registerViewModel.isUserRegistered.observeAsState(initial = false)
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
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
                            registered,
                            latitude.takeIf { it != 0.0 },
                            longitude.takeIf { it != 0.0 }
                        )
                    }
                }
            }
        }
        checkLocationPermissions()
    }



    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            startLocationService()
        }
    }

    private fun startLocationService() {
        val intent = Intent(this, LocationUpdateService::class.java)
        startService(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startLocationService()
            } else {
                Toast.makeText(this, "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




/*
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ProyectoFinalDAMDTheme {
        AppNavigation()
    }
}
*/


