package com.fedeyruben.proyectofinaldamd.data.dataStore.repository

import com.fedeyruben.proyectofinaldamd.data.dataStore.model.UserData
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun saveAllData(phone: String, code: String)
    suspend fun savePhoneNumber(phone: String)
    suspend fun saveCodePhone(code: String)
    suspend fun deleteUser()
    fun getAllDataUser(): Flow<UserData>
}