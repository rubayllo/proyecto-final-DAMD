package com.fedeyruben.proyectofinaldamd.data.dataStore.model
data class UserData(
    val name: String,
    val password: String,
    val userLogged: Boolean,
    val authorizationHeader: String,
    val simetricKey: String,
    val loginJWT: String
)
