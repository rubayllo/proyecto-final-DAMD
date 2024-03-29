package com.fedeyruben.proyectofinaldamd.data.firebase.realTimeDataBase

import android.util.Log
import com.fedeyruben.proyectofinaldamd.data.firebase.realTimeDataBase.model.User
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await


object RealTimeDBManager {

    // Guardo la referencia de la base de datos del proyecto
    private val dbReference = FirebaseDatabase.getInstance().reference


    /** Método para actualizar la ubicación del usuario en la base de datos en tiempo real */
     suspend fun updateUserLocation(userId: String, location: LatLng) {
        val userReference = dbReference.child("users").child(userId)
        userReference.child("location").setValue(location).await()
    }


    /** Método para almacenar el usuario por primera vez */
    suspend fun saveUser(name: String, location: LatLng, userId: String): Boolean {

        // Para guardar los usuarios en una subCarpeta "users"
        val connection = dbReference.child("users")

        // Creo una key única para poder guardar el user
        val key = connection.push().key

        if (key != null) {
            // Creo el objeto con los parametros que me van llegando en la funcion
            val user = User(key, userId, name, location)
            // Lo guardo , creo un child dentro de la subCarpeta user
            // Con una key que identifique el objeto y dentro guardo el objeto
            connection.child(key).setValue(user).await()
            return true
        } else {
            Log.d("saveUser", "error")
            return false
        }
    }


    /** Recuperar los datos de un usuario mediante id (Location) */
    suspend fun getUserById(userId: String): User? {
        val connection = dbReference.child("users")
        val result = connection.orderByKey().get().await()

        return result.children.mapNotNull { dataSnapshot ->
            val data = dataSnapshot.getValue(User::class.java)
            if (!data?.key.isNullOrEmpty()
                && !data?.name.isNullOrEmpty()
                && data?.userId == userId
                && data?.location != null
            ) {
                return@mapNotNull data
            } else {
                return@mapNotNull null
            }
        }.firstOrNull()
    }


    /** Recuperar toda la lista de usuarios con sus ubicaciones */
    /** TODO PODEMOS CREAR OTRA CHILD CON USER_FAV para cada uno de los usuarios*/
    suspend fun getUsers(): List<User> {
        val connection = dbReference.child("users")
        val result = connection.orderByKey().get().await() // Ordenamos por como se fueron guardando
        val userList: MutableList<User> =
            mutableListOf() // Lista para guardar los objetos que me van llegando

        if (result.childrenCount > 0) {
            result.children.mapNotNull { dataSnapshot ->
                val data = dataSnapshot.getValue(User::class.java)
                if (!data?.key.isNullOrEmpty()
                    && !data?.name.isNullOrEmpty()
                    && !data?.userId.isNullOrEmpty()
                    && data?.location != null
                ) {
                    userList.add(data!!)
                }
            }
        }
        return userList.toList()
    }

    fun deleteUser(key: String) {
        val connection = dbReference.child("users")
        connection.child(key).removeValue()
    }
}

