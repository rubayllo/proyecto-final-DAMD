package com.fedeyruben.proyectofinaldamd.data.dataStore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
    private val codePhone = stringPreferencesKey("user_code_phone")

    /** Funcion para almacenar los datos en local necesito contexto y clave **/
    override suspend fun saveAllData(phone: String, code: String) {
        dataStore.edit { editor ->
            editor[phoneNumber] = phone
            editor[codePhone] = code
        }
    }

    override suspend fun savePhoneNumber(phone: String) {
        dataStore.edit { editor ->
            editor[phoneNumber] = phone
        }
    }

    override suspend fun saveCodePhone(code: String) {
        dataStore.edit { editor ->
            editor[codePhone] = code
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
                codePhone = preferences[stringPreferencesKey("user_code_phone")].orEmpty()
            )
        }
    }
}



