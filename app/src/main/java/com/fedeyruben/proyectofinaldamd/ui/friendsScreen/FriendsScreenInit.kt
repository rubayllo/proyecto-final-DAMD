package com.fedeyruben.proyectofinaldamd.ui.friendsScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.friends.FriendsViewModel
import com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation.bottomBarHeight

@Composable
fun ListaAmigosScreen(friendsViewModel: FriendsViewModel) {

    val amigos by friendsViewModel.userGuardiansContactsList.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomBarHeight),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(amigos) { amigo ->
            AmigoListItem(amigo, friendsViewModel)
        }
    }
}

@Composable
fun AmigoListItem(
    amigo: UserGuardiansContacts,
    friendsViewModel: FriendsViewModel
) {
    val guardianAlertLevelList by friendsViewModel.guardianAlertLevelList.collectAsState()
    val guardianAlertLevel = guardianAlertLevelList.find { it.userGuardianId == amigo.guardianPhoneNumber }

    val expanded = rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Usar Coil para cargar la imagen desde una Uri
            Image(
                painter = if (amigo.guardianImage != null) rememberImagePainter(amigo.guardianImage) else painterResource(
                    id = R.drawable.person
                ),
                contentDescription = "Imagen de perfil de ${amigo.guardianName}",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = "${amigo.guardianName} ${amigo.guardianSurname ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))


                // Fila para botones, alineada a la derecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            expanded.value = !expanded.value
                        }
                    ) {
                        Text(text = if (expanded.value) "Reducir" else "Ampliar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { friendsViewModel.confirmDelete(amigo) }
                    ) {
                        Text(
                            text = "Eliminar",
                            color = Color.Red
                        )
                    }
                }
            }
        }
        // Contenido expandido
        if (expanded.value) {

                Log.d("AmigoListItem", "guardianAlertLevel: ${guardianAlertLevel?.userGuardianId}")


            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Alerta baja")
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            friendsViewModel.updateLowColumn(amigo.guardianPhoneNumber, 1, !guardianAlertLevel?.low!!)
                        }
                    ) {
                        Text(
                            text = if (guardianAlertLevel?.low!!) "Desactivar" else "Activar",
                            color = if (guardianAlertLevel.low) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Alerta media")
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            friendsViewModel.updateLowColumn(amigo.guardianPhoneNumber, 2, !guardianAlertLevel?.medium!!)
                        }                    ) {
                        Text(
                            text = if (guardianAlertLevel?.medium!!) "Desactivar" else "Activar",
                            color = if (guardianAlertLevel.medium) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Alerta alta")
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            friendsViewModel.updateLowColumn(amigo.guardianPhoneNumber, 3, !guardianAlertLevel?.high!!)
                        }                    ) {
                        Text(
                            text = if (guardianAlertLevel?.high!!) "Desactivar" else "Activar",
                            color = if (guardianAlertLevel.high) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Alerta crítica")
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            friendsViewModel.updateLowColumn(amigo.guardianPhoneNumber, 4, !guardianAlertLevel?.critical!!)
                        }                    ) {
                        Text(
                            text = if (guardianAlertLevel?.critical!!) "Desactivar" else "Activar",
                            color = if (guardianAlertLevel.critical) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun FriendsScreenInit(friendsViewModel: FriendsViewModel) {
    val contactToDelete by friendsViewModel.contactToDelete.collectAsState()
    val showDuplicateDialog by friendsViewModel.showDuplicateDialog.collectAsState()

    ListaAmigosScreen(friendsViewModel)

    showDuplicateDialog?.let { phoneNumber ->
        AlertDialog(
            onDismissRequest = { friendsViewModel.dismissDuplicateDialog() },
            confirmButton = {
                TextButton(onClick = { friendsViewModel.dismissDuplicateDialog() }) {
                    Text("OK")
                }
            },
            title = { Text("El usuario con el número de teléfono $phoneNumber ya está registrado.") }
        )
    }

    contactToDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { friendsViewModel.dismissDeleteDialog() },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro que quiere eliminar al usuario ${contact.guardianName}?") },
            confirmButton = {
                TextButton(onClick = { friendsViewModel.deleteGuardian(contact); friendsViewModel.dismissDeleteDialog() }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { friendsViewModel.dismissDeleteDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
