package com.fedeyruben.proyectofinaldamd.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fedeyruben.proyectofinaldamd.R

data class Friend(val id: Int, val nombre: String, val apellido: String)

@Composable
fun ListaAmigosScreen() {
    // Luego vendrá de la Base de datos
    val amigos = listOf(
        Friend(1, "Fede", "González"),
        Friend(2, "Ruben", "Díaz"),
        Friend(3, "Pamela", "Martínez"),
        Friend(4, "Cintia", "Rodríguez"),
        Friend(5, "Claudia", "Pérez")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(amigos) { amigo ->
            AmigoListItem(amigo = amigo)
        }
    }

}


@Composable
fun AmigoListItem(amigo: Friend) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.person),
                contentDescription = "Imagen de ${amigo.nombre}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                   // .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)) // Fondo leve detrás del avatar
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${amigo.nombre} ${amigo.apellido}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun FriendsScreenInit() {
    ListaAmigosScreen()
}
