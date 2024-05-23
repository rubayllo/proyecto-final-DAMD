package com.fedeyruben.proyectofinaldamd.ui.friendsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.fedeyruben.proyectofinaldamd.R
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.friends.FriendsViewModel

@Composable
fun ListaAmigosScreen(friendsViewModel: FriendsViewModel) {

    val amigos by friendsViewModel.userGuardiansContactsList.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(amigos) { amigo ->
            AmigoListItem(amigo, friendsViewModel)
        }
    }
}

@Composable
fun AmigoListItem(amigo: UserGuardiansContacts, friendsViewModel: FriendsViewModel) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Asegura que los elementos se distribuyen correctamente
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${amigo.guardianName} ${amigo.guardianSurname ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
            IconButton(
                onClick = { friendsViewModel.confirmDelete(amigo) },
                modifier = Modifier
                    .size(48.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { friendsViewModel.confirmDelete(amigo) }
                        )
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar contacto",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
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
