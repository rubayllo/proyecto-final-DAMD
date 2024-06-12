package com.fedeyruben.proyectofinaldamd.ui.settingsScreen

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedeyruben.proyectofinaldamd.data.room.UserDatabaseDaoRepositoryImp
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.data.room.model.UserProtected
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val userDatabaseDaoRepositoryImp: UserDatabaseDaoRepositoryImp) :
    ViewModel() {

    // Observa los contactos almacenados en Room
    private val _userGuardiansContactsList =
        MutableStateFlow<List<UserGuardiansContacts>>(emptyList())
    val userGuardiansContactsList = _userGuardiansContactsList.asStateFlow()

    // Observa el nivel de alerta de los guardianes
    private val _guardianAlertLevel = MutableStateFlow<GuardianAlertLevel?>(null)
    val guardianAlertLevel: StateFlow<GuardianAlertLevel?> = _guardianAlertLevel

    // Observa la lista de niveles de alerta de los guardianes
    private val _guardianAlertLevelList =
        MutableStateFlow<List<GuardianAlertLevel>>(emptyList())
    val guardianAlertLevelList = _guardianAlertLevelList.asStateFlow()

    // Observa la lista de contactos de los guardianes protegidos
    private val _protectedGuardiansContactsList =
        MutableStateFlow<List<UserProtected>>(emptyList())
    val protectedGuardiansContactsList = _protectedGuardiansContactsList.asStateFlow()

    // Firebase firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userDatabaseDaoRepositoryImp.getAllGuardians().collect { item ->
                if (item.isNotEmpty()) {
                    _userGuardiansContactsList.value = emptyList()
                }
                _userGuardiansContactsList.value = item.sortedBy { it.guardianName }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            userDatabaseDaoRepositoryImp.getAllAlertsOfGuardians().collect { item ->
                if (item.isNotEmpty()) {
                    _guardianAlertLevelList.value = emptyList()
                }
                _guardianAlertLevelList.value = item
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            userDatabaseDaoRepositoryImp.getAllRequestsProtected().collect { item ->
                if (item.isNotEmpty()) {
                    _protectedGuardiansContactsList.value = emptyList()
                }
                _protectedGuardiansContactsList.value = item.sortedBy { it.userProtectedName }
                Log.d("SettingsViewModelInit", "ProtectedGuardiansContactsList: $item")
            }
        }

    }

     fun iniciarFirestoreRecogerProtegidos(context: Context) {
        // Recoge la coleccion de Firestore de los contactos protegidos y los incluye en la lista
        val thisPhoneUser = auth.currentUser?.phoneNumber

        Log.d("SettingsViewModelInit", "This phone user: $thisPhoneUser")

        // Recoge el array de request desde la colección guardian_request
        firestore.collection("guardian_request")
            .whereEqualTo("userPhone", thisPhoneUser)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("SettingsViewModelInit", "Listen failed.", error)
                    return@addSnapshotListener
                }
                for (doc in value!!) {
                    val requestList = doc.get("request") as List<Map<String, Boolean>>
                    requestList.forEach { request ->
                        Log.d("SettingsViewModelInit", "Request: $request")
                        val phoneNumber = request.keys.first()
                        val isAccepted = request.values.first()
                        val userProtected = UserProtected(phoneNumber, recuperarNombreTelefono(context, phoneNumber), isAccepted)

                        // Si el contacto no está en la lista de contactos protegidos, lo añade
                        // Y si el contacto está en la lista de contactos protegidos, lo actualiza
                        if (_protectedGuardiansContactsList.value.none { it.userPhoneProtected == phoneNumber }) {
                            viewModelScope.launch {
                                userDatabaseDaoRepositoryImp.insertRequestProtected(userProtected)
                            }
                        } else {
                            viewModelScope.launch {
                                userDatabaseDaoRepositoryImp.updateRequestProtected(userProtected)
                            }
                        }
                    }
                }
            }
    }


    fun updateGuardianAlertLevel(phoneNumber: String, level: Int, newValue: Boolean) {
        viewModelScope.launch {
            when (level) {
                0 -> userDatabaseDaoRepositoryImp.updateLowColumn(phoneNumber, newValue)
                1 -> userDatabaseDaoRepositoryImp.updateMediumColumn(phoneNumber, newValue)
                2 -> userDatabaseDaoRepositoryImp.updateHighColumn(phoneNumber, newValue)
                3 -> userDatabaseDaoRepositoryImp.updateCriticalColumn(phoneNumber, newValue)
            }
        }
    }

    fun updateIsGuardianRegister(userPhoneProtected: String, isAccepted: Boolean, context: Context) {
        val thisPhoneUser = auth.currentUser?.phoneNumber

        // Consulta Firestore para obtener el documento con el array de request
        firestore.collection("guardian_request")
            .whereEqualTo("userPhone", thisPhoneUser)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val requestArray = document.get("request") as? MutableList<Map<String, Boolean>> ?: mutableListOf()

                    // Encuentra y actualiza el mapa específico en el array
                    val updatedRequestArray = requestArray.map { request ->
                        val phone = request.keys.firstOrNull()
                        if (phone == userPhoneProtected) {
                            mapOf(phone to isAccepted)
                        } else {
                            request
                        }
                    }

                    // Actualiza Firestore con el array modificado
                    firestore.collection("guardian_request").document(document.id)
                        .update("request", updatedRequestArray)
                        .addOnSuccessListener {
                            Log.d("SettingsViewModelUpdate", "DocumentSnapshot successfully updated!")

                            // Actualiza la base de datos local
                            val userProtected = UserProtected(userPhoneProtected, recuperarNombreTelefono(context, userPhoneProtected),isAccepted)
                            viewModelScope.launch(Dispatchers.IO) {
                                userDatabaseDaoRepositoryImp.updateRequestProtected(userProtected)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("SettingsViewModelUpdate", "Error updating document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("SettingsViewModelUpdate", "Error getting documents: ", e)
            }
    }

    fun recuperarNombreTelefono(context: Context, userPhoneProtected: String): String {
        var contactName = userPhoneProtected.trim() // Eliminar espacios en blanco al principio y al final

        // Normalizar el número proporcionado
        val userPhoneNormalized = userPhoneProtected.replace("\\s+".toRegex(), "").removePrefix("+")

        // Consulta la agenda telefónica para obtener el nombre del contacto
        val resolver: ContentResolver = context.contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // Comparación flexible de números normalizados
        val selection = "REPLACE(REPLACE(${ContactsContract.CommonDataKinds.Phone.NUMBER}, ' ', ''), '+', '') = REPLACE(REPLACE(?, ' ', ''), '+', '')"
        val selectionArgs = arrayOf(userPhoneNormalized)

        // Realizar consulta a la agenda telefónica
        val cursor: Cursor? = resolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                contactName = it.getString(nameIndex)
            }
        }

        if(contactName == userPhoneProtected) {
            contactName = "Desconocido $userPhoneProtected"
        }

        return contactName
    }

    // Funcion para contar cuantos protegidosOrdenados hay en el listado de protegidos y cuantos en solicitudes de proteccion
    fun countProtectedAndRequests(
        protectedGuardiansContactsList: List<UserProtected>
    ): Pair<Int, Int> {
        var protected = 0
        var requests = 0
        protectedGuardiansContactsList.forEach {
            if (it.isProtected) {
                protected++
            } else {
                requests++
            }
        }
        return Pair(protected, requests)
    }

}