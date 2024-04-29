package com.fedeyruben.proyectofinaldamd.data.dataStore.repository

import android.content.Context
import com.fedeyruben.proyectofinaldamd.data.dataStore.model.UserData
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveData(email: String, password: String)
    suspend fun saveAuthorizationHeader(userPassBase: String)
    suspend fun saveUserJWT(apiKeyResponse: String, loginResponse: String)
    suspend fun setUserLogged(isLogged: Boolean)
    suspend fun deleteLogin()
    suspend fun deleteUser()
    suspend fun getIsUserLogged(): Flow<Boolean>
    fun getAllDataUser(): Flow<UserData>
    suspend fun getAuthorizationHeader(): Flow<String>
}