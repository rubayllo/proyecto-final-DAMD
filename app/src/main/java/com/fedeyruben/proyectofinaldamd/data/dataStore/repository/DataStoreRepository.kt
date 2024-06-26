package com.fedeyruben.proyectofinaldamd.data.dataStore.repository

import com.fedeyruben.proyectofinaldamd.data.dataStore.model.UserData
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveAllData(phone: String, isRegister: Boolean)
    suspend fun deleteUser()
    suspend fun getUserPhone(): String
    fun getAllDataUser(): Flow<UserData>
}