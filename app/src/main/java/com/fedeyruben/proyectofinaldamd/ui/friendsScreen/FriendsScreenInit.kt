package com.fedeyruben.proyectofinaldamd.ui.friendsScreen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.ui.navigation.bottomNavigation.bottomBarHeight
import com.fedeyruben.proyectofinaldamd.ui.theme.AlertLowColor
import com.fedeyruben.proyectofinaldamd.ui.theme.BlueColorStyle

@Composable
fun ListaAmigosScreen(friendsViewModel: FriendsViewModel) {
    // Obtener la lista de amigos observando el estado del ViewModel
    val amigos by friendsViewModel.userGuardiansContactsList.collectAsState()

    // Ordenar la lista de amigos alfabéticamente por nombre de contacto
    val amigosOrdenados = amigos.sortedBy { it.guardianName }

    // LazyColumn para mostrar la lista de amigos con un relleno personalizado y separación entre elementos
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 1.dp,
            end = 1.dp,
            top = 16.dp,
            bottom = bottomBarHeight // Altura de la barra inferior
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre elementos
    ) {
        // Iterar sobre cada amigo en la lista y mostrar AmigoListItem
        items(amigosOrdenados) { amigo ->
            AmigoListItem(amigo, friendsViewModel)
        }
    }
}


@Composable
fun AmigoListItem(
    amigo: UserGuardiansContacts,
    friendsViewModel: FriendsViewModel,
) {
    // Obtener la lista de niveles de alerta de los guardianes observando el estado del ViewModel
    val guardianAlertLevelList by friendsViewModel.guardianAlertLevelList.collectAsState()
    val guardianAlertLevel =
        guardianAlertLevelList.find { it.userGuardianId == amigo.guardianPhoneNumber }

    // Verificar si el usuario está registrado
    friendsViewModel.isUserSignedIn(amigo.guardianPhoneNumber)

    // Obtener el contexto actual
    val context = LocalContext.current

    // Estado para expandir o reducir el contenido del item
    val expanded = rememberSaveable { mutableStateOf(false) }

    // Tarjeta para mostrar la información del amigo
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)), // Color de fondo
        shape = RoundedCornerShape(8.dp), // Esquina redondeada
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Elevación de la tarjeta
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically // Alinear verticalmente al centro
        ) {
            // Usar Coil para cargar la imagen desde una Uri
//            Image(
//                painter = painterResource(
//                    id = R.drawable.person // Imagen de perfil por defecto
//                ),
//                contentDescription = "Imagen de perfil de ${amigo.guardianName}",
//                modifier = Modifier
//                    .size(54.dp) // Tamaño de la imagen
//                    .clip(CircleShape), // Forma circular
//                contentScale = ContentScale.Crop // Escalar imagen para que llene el contenedor
//            )
//
//            Spacer(modifier = Modifier.width(24.dp)) // Espacio entre imagen y texto

            Column(
                modifier = Modifier
                    .weight(1f) // Tomar el espacio disponible
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${amigo.guardianName} ${amigo.guardianSurname}", // Nombre y apellido del guardián
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    // Texto clickable para eliminar al guardián
                    Text(
                        text = stringResource(id = R.string.delete), // Número de teléfono del guardián
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red, // Color rojo
                        modifier = Modifier.clickable {
                            friendsViewModel.confirmDelete(amigo)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre nombre y la fila de debajo
                // Fila para botones, alineada a la derecha
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (!amigo.isGuardianRegister) {
                        // Texto clickable para invitar al guardián a la app
                        Text(
                            text = stringResource(id = R.string.invite), // Número de teléfono del guardián
                            style = MaterialTheme.typography.bodyMedium,
                            color = BlueColorStyle, // Color rojo
                            modifier = Modifier.clickable {
                                friendsViewModel.inviteToApp(amigo.guardianPhoneNumber, context)
                            }
                        )

                    } else if (!amigo.isGuardianActive) {
                        Text(
                            text = stringResource(id = R.string.waiting),
                            style = MaterialTheme.typography.bodyMedium, // Mismo estilo que TextButton
                            color = AlertLowColor // Color verde
                        )

                    } else {
                        // Texto clickable para invitar al guardián a la app
                        Text(
                            text = if (expanded.value) stringResource(id = R.string.hide)
                            else stringResource(id = R.string.asignar), // Número de teléfono del guardián
                            style = MaterialTheme.typography.bodyMedium,
                            color = BlueColorStyle, // Color rojo
                            modifier = Modifier.clickable {
                                // Expandir o reducir el contenido
                                expanded.value = !expanded.value
                            }
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
                // Nivel de alerta baja
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Alerta baja")
                    Spacer(modifier = Modifier.weight(1f)) // Espacio flexible
                    TextButton(
                        onClick = {
                            friendsViewModel.updateGuardianAlertLevel(
                                amigo.guardianPhoneNumber,
                                0,
                                !guardianAlertLevel?.low!!
                            )
                        }
                    ) {
                        Text(
                            text = if (guardianAlertLevel?.low!!) stringResource(id = R.string.desactivar)
                            else stringResource(id = R.string.asignar),
                            color = if (guardianAlertLevel.low) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }
                // Nivel de alerta media
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.alert_medium))
                    Spacer(modifier = Modifier.weight(1f)) // Espacio flexible
                    TextButton(
                        onClick = {
                            friendsViewModel.updateGuardianAlertLevel(
                                amigo.guardianPhoneNumber,
                                1,
                                !guardianAlertLevel?.medium!!
                            )
                        }) {
                        Text(
                            text = if (guardianAlertLevel?.medium!!)  stringResource(id = R.string.desactivar)
                            else stringResource(id = R.string.asignar),
                            color = if (guardianAlertLevel.medium) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }
                // Nivel de alerta alta
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.alert_high))
                    Spacer(modifier = Modifier.weight(1f)) // Espacio flexible
                    TextButton(
                        onClick = {
                            friendsViewModel.updateGuardianAlertLevel(
                                amigo.guardianPhoneNumber,
                                2,
                                !guardianAlertLevel?.high!!
                            )
                        }) {
                        Text(
                            text = if (guardianAlertLevel?.high!!)  stringResource(id = R.string.desactivar)
                            else stringResource(id = R.string.asignar),
                            color = if (guardianAlertLevel.high) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }
                // Nivel de alerta crítica
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = stringResource(id = R.string.alert_critical))
                    Spacer(modifier = Modifier.weight(1f)) // Espacio flexible
                    TextButton(
                        onClick = {
                            friendsViewModel.updateGuardianAlertLevel(
                                amigo.guardianPhoneNumber,
                                3,
                                !guardianAlertLevel?.critical!!
                            )
                        }) {
                        Text(
                            text = if (guardianAlertLevel?.critical!!) stringResource(id = R.string.desactivar)
                            else stringResource(id = R.string.asignar),
                            color = if (guardianAlertLevel.critical) Color.Red else Color.Unspecified // Cambia a rojo si está desactivado
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp)) // Espacio al final del contenido expandido
            }
        }
    }
}

@Composable
fun FriendsScreenInit(friendsViewModel: FriendsViewModel) {
    // Obtener estados observables del ViewModel
    val contactToDelete by friendsViewModel.contactToDelete.collectAsState()
    val showDuplicateDialog by friendsViewModel.showDuplicateDialog.collectAsState()

    // Mostrar la lista de amigos
    ListaAmigosScreen(friendsViewModel)

    // Mostrar diálogo de duplicado si es necesario
    showDuplicateDialog?.let { phoneNumber ->
        AlertDialog(
            onDismissRequest = { friendsViewModel.dismissDuplicateDialog() },
            confirmButton = {
                TextButton(onClick = { friendsViewModel.dismissDuplicateDialog() }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            title = { Text(stringResource(id = R.string.duplicate_number_title)
                    + " $phoneNumber "
                    + stringResource(id = R.string.duplicate_number_title2)) }
        )
    }

    // Mostrar diálogo de confirmación de eliminación si es necesario
    contactToDelete?.let { contact ->
        AlertDialog(
            onDismissRequest = { friendsViewModel.dismissDeleteDialog() },
            title = { Text(stringResource(id = R.string.delete_confirmation)) },
            text = { Text(stringResource(id = R.string.delete_confirmation_text)+ " ${contact.guardianName}?") },
            confirmButton = {
                TextButton(onClick = { friendsViewModel.deleteGuardian(contact); friendsViewModel.dismissDeleteDialog() }) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { friendsViewModel.dismissDeleteDialog() }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}
