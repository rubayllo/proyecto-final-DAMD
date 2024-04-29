package com.fedeyruben.proyectofinaldamd

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
import androidx.compose.ui.Modifier
import com.fedeyruben.proyectofinaldamd.friends.FriendsViewModel
import com.fedeyruben.proyectofinaldamd.navigation.AppNavigation
import com.fedeyruben.proyectofinaldamd.ui.theme.ProyectoFinalDAMDTheme

class MainActivity : ComponentActivity() {

    private val friendsViewModel : FriendsViewModel by viewModels()

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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(pickContactResultLauncher,friendsViewModel)
                }
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
