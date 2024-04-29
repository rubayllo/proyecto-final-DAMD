package com.fedeyruben.proyectofinaldamd.data.dataStore.repository

import android.content.Context
import android.util.Log
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

    /** Claves para acceder a los datos **/
    private val emailKey = stringPreferencesKey("email")
    private val passwordKey = stringPreferencesKey("password")
    private val userLogged = booleanPreferencesKey("user_logged")
    private val authorizationHeader = stringPreferencesKey("authorization_header")
    private val simetricKey = stringPreferencesKey("simetric_key")
    private val loginJWT = stringPreferencesKey("user_token_jwt")

    /** Funcion para almacenar los datos en local necesito contexto y clave **/
    override suspend fun saveData(email: String, password: String) {
        dataStore.edit { editor ->
            editor[emailKey] = email
            editor[passwordKey] = password
            editor[userLogged] = true
        }
    }

    /**  Guardar authorization base64 **/
    override suspend fun saveAuthorizationHeader(userPassBase: String) {
        dataStore.edit { editor ->
            editor[authorizationHeader] = userPassBase
        }
    }

    /**  Guardar simetriKwt y LoginJWT **/
    override suspend fun saveUserJWT(apiKeyResponse: String, loginResponse: String) {
        dataStore.edit { editor ->
            editor[simetricKey] = apiKeyResponse
            editor[loginJWT] = loginResponse
        }
    }

    override suspend fun getAuthorizationHeader() : Flow<String> {
        Log.i("entra","getAuth")
       return dataStore.data
            .map { preferences ->
                preferences[loginJWT] ?: ""
            }

    }

    /**  Cambio el estado de isLogged **/
    override suspend fun setUserLogged(isLogged: Boolean) {
        dataStore.edit { editor ->
            editor[userLogged] = isLogged;
        }
    }

    /**  Cambio el estado de isLogged, el usuario cierra sesión **/
    override suspend fun deleteLogin() {
        setUserLogged(false)
    }

    /** Elimino el usuario con todos sus datos **/
    override suspend fun deleteUser() {
        dataStore.edit { editor ->
            editor.clear()
        }
    }

    /** Compruebo si el usuario esta loggeado **/
    override suspend fun getIsUserLogged(): Flow<Boolean> {
        return dataStore.data.map { editor ->
            editor[userLogged] ?: false
        }
    }

    /** Funcion para mapear todos los datos juntos **/
    override fun getAllDataUser(): Flow<UserData> {
        return dataStore.data.map { preferences ->
            UserData(
                name = preferences[stringPreferencesKey("email")].orEmpty(),
                password = preferences[stringPreferencesKey("password")].orEmpty(),
                userLogged = preferences[booleanPreferencesKey("user_logged")] ?: false,
                authorizationHeader = preferences[stringPreferencesKey("authorization_header")].orEmpty(),
                simetricKey = preferences[stringPreferencesKey("simetric_key")].orEmpty(),
                loginJWT = preferences[stringPreferencesKey("user_token_jwt")].orEmpty()
            )
        }
    }
}



