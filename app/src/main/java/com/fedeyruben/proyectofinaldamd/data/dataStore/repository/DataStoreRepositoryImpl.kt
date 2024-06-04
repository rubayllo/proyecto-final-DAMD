package com.fedeyruben.proyectofinaldamd.data.dataStore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.fedeyruben.proyectofinaldamd.data.dataStore.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    /** Clave para acceder a las credenciales del usuario **/
    private val phoneNumber = stringPreferencesKey("user_phone_number")
    private val isRegister = booleanPreferencesKey("is_register")

    /** Funcion para almacenar los datos en local necesito contexto y clave **/
    override suspend fun saveAllData(phone: String, isRegister: Boolean) {
        dataStore.edit { editor ->
            editor[phoneNumber] = phone
            editor[this.isRegister] = isRegister
        }
    }


    /** Elimino el usuario con todos sus datos **/
    override suspend fun deleteUser() {
        dataStore.edit { editor ->
            editor.clear()
        }
    }

    /** Funcion para mapear todos los datos juntos **/
    override fun getAllDataUser(): Flow<UserData> {
        return dataStore.data.map { preferences ->
            UserData(
                phoneNumber = preferences[stringPreferencesKey("user_phone_number")].orEmpty(),
                isRegister = preferences[booleanPreferencesKey("is_register")] ?: false
            )
        }
    }
}



