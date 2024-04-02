package com.fedeyruben.proyectofinaldamd.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(amigos) { amigo ->
            AmigoListItem(amigo = amigo)
        }
    }
}

@Composable
fun AmigoListItem(amigo: Friend) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Usando Image en lugar de Icon
            Image(
                painter = painterResource(id = R.drawable.person),
                contentDescription = "Imagen de perfil de ${amigo.nombre}",
                modifier = Modifier
                    .size(48.dp) // Ajusta el tamaño según necesites
                    .clip(CircleShape), // Hace que la imagen sea circular
                contentScale = ContentScale.Crop // Asegura que la imagen se recorte para ajustarse
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${amigo.nombre} ${amigo.apellido}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@Composable
fun FriendsScreenInit() {
    ListaAmigosScreen()
}
