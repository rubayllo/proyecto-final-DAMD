package com.fedeyruben.proyectofinaldamd.ui.friendsScreen

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedeyruben.proyectofinaldamd.data.room.UserDatabaseDaoRepositoryImp
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.utils.LocationUpdateService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class FriendsViewModel @Inject constructor(private val userDatabaseDaoRepositoryImp: UserDatabaseDaoRepositoryImp) :
    ViewModel() {
    // Estado para el diálogo
    private val _showDuplicateDialog = MutableStateFlow<String?>(null)
    val showDuplicateDialog: StateFlow<String?> = _showDuplicateDialog

    // Estado para el diálogo de confirmación de eliminación
    private val _contactToDelete = MutableStateFlow<UserGuardiansContacts?>(null)
    val contactToDelete: StateFlow<UserGuardiansContacts?> = _contactToDelete

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

    // Firebase firestore
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userDatabaseDaoRepositoryImp.getAllGuardians().collect { item ->
                if (item.isNotEmpty()) {
                    _userGuardiansContactsList.value = emptyList()
                }
                _userGuardiansContactsList.value = item
            }
            Log.d("RoomFriendsViewModel", "init: ${_userGuardiansContactsList.value}")
        }
        viewModelScope.launch(Dispatchers.IO) {
            userDatabaseDaoRepositoryImp.getAllAlertsOfGuardians().collect { item ->
                if (item.isNotEmpty()) {
                    _guardianAlertLevelList.value = emptyList()
                }
                _guardianAlertLevelList.value = item
            }
        }
    }

    fun isUserSignedIn(verifyPhoneUserRegister: String) {
        firestore.collection("users")
            .whereEqualTo("phoneUser", verifyPhoneUserRegister)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("FriendsViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    for (doc in value) {
                        Log.d(
                            "FriendsViewModel",
                            "$verifyPhoneUserRegister : ${doc.id} => ${doc.data}"
                        )
                        viewModelScope.launch(Dispatchers.IO) {
                            userDatabaseDaoRepositoryImp.updateIsGuardianRegister(
                                verifyPhoneUserRegister,
                                true
                            )
                            isUserActive(verifyPhoneUserRegister)
                        }
                    }
                    if (value.isEmpty) {
                        Log.d(
                            "FriendsViewModel",
                            "$verifyPhoneUserRegister : Phone Number No Registered"
                        )
                        viewModelScope.launch(Dispatchers.IO) {
                            userDatabaseDaoRepositoryImp.updateIsGuardianRegister(
                                verifyPhoneUserRegister,
                                false
                            )
                            userDatabaseDaoRepositoryImp.updateIsGuardianActive(
                                verifyPhoneUserRegister,
                                false
                            )
                        }
                    }
                } else {
                    Log.d("FriendsViewModel", "No such document")
                }
            }
    }

    private fun isUserActive(verifyPhoneUserRegister: String) {
        val thisPhoneUser = auth.currentUser?.phoneNumber

        firestore.collection("guardian_request")
            .whereEqualTo("userPhone", verifyPhoneUserRegister)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("FriendsViewModelActive", "Listen failed.", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    for (doc in value) {
                        Log.d(
                            "FriendsViewModelActive2",
                            "$verifyPhoneUserRegister : ${doc.id} => ${doc.data}"
                        )
                        // Obtener el array 'request' del documento
                        val requestList = doc.get("request") as List<Map<String, Boolean>>
                        Log.d(
                            "FriendsViewModelActive2",
                            "$verifyPhoneUserRegister : $requestList"
                        )
                        requestList.forEach { request ->
                            if (request[thisPhoneUser] == true) {
                                Log.d(
                                    "FriendsViewModelActive2",
                                    "$verifyPhoneUserRegister : Si Acepta tu petición"
                                )

                                // Actualizar el estado en la base de datos local
                                viewModelScope.launch(Dispatchers.IO) {
                                    userDatabaseDaoRepositoryImp.updateIsGuardianActive(
                                        verifyPhoneUserRegister,
                                        true
                                    )
                                }
                            } else if (request[thisPhoneUser] == false) {
                                Log.d(
                                    "FriendsViewModelActive2",
                                    "$verifyPhoneUserRegister : No Acepta tu petición"
                                )
                                viewModelScope.launch(Dispatchers.IO) {
                                    userDatabaseDaoRepositoryImp.updateIsGuardianActive(
                                        verifyPhoneUserRegister,
                                        false
                                    )
                                }
                            }
                        }
                    }

                    if (value.isEmpty) {
                        Log.d(
                            "FriendsViewModelActive",
                            "$verifyPhoneUserRegister : No Acepta tu petición"
                        )
                        viewModelScope.launch(Dispatchers.IO) {
                            userDatabaseDaoRepositoryImp.updateIsGuardianActive(
                                verifyPhoneUserRegister,
                                false
                            )
                        }
                    }
                } else {
                    Log.d("FriendsViewModelActive", "No such document")
                    viewModelScope.launch(Dispatchers.IO) {
                        userDatabaseDaoRepositoryImp.updateIsGuardianActive(
                            verifyPhoneUserRegister,
                            false
                        )
                    }
                }
            }
    }

    private fun addGuardian(
        userGuardiansContacts: UserGuardiansContacts,
        guardianAlertLevel: GuardianAlertLevel
    ) {
        val thisPhoneUser = auth.currentUser?.phoneNumber

        viewModelScope.launch {
            userDatabaseDaoRepositoryImp.insertGuardian(userGuardiansContacts)
            userDatabaseDaoRepositoryImp.insertGuardianAlertLevel(guardianAlertLevel)
        }

        val userPhone = userGuardiansContacts.guardianPhoneNumber

        firestore.collection("guardian_request")
            .whereEqualTo("userPhone", userPhone)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Usuario no está registrado, agregar a Firestore
                    val newUser = hashMapOf(
                        "userPhone" to userPhone,
                        "request" to arrayListOf(
                            hashMapOf(
                                thisPhoneUser to false
                            )
                        )
                    )

                    firestore.collection("guardian_request").add(newUser)
                        .addOnSuccessListener {
                            Log.d("FriendsViewModel", "User added successfully")
                        }
                        .addOnFailureListener {
                            Log.d("ERROR SAVE USER 1", it.message.toString())
                        }
                } else {
                    // Usuario ya está registrado, actualizar en Firestore
                    Log.d("FriendsViewModel", "User already registered: $userPhone")
                    for (document in documents) {
                        val requestList = document.get("request") as? List<Map<String, Boolean>>
                        val existingGuardian =
                            requestList?.any { it.containsKey(thisPhoneUser) } == true

                        if (!existingGuardian) {
                            // El guardián no está en el array, agregarlo
                            val newGuardian = mapOf(thisPhoneUser to false)
                            firestore.collection("guardian_request")
                                .document(document.id)
                                .update("request", FieldValue.arrayUnion(newGuardian))
                                .addOnSuccessListener {
                                    Log.d(
                                        "FriendsViewModel",
                                        "Guardian added to existing document with ID: ${document.id}"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w("FriendsViewModel", "Error updating document", e)
                                }
                        } else {
                            Log.d(
                                "FriendsViewModel",
                                "Guardian already exists in the request array"
                            )
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("FriendsViewModel", "Error getting documents", e)
            }
    }


    fun deleteGuardian(userGuardiansContacts: UserGuardiansContacts) {
        // Eliminar guardian de Firestore
        val thisPhoneUser = auth.currentUser?.phoneNumber
        val userPhone = userGuardiansContacts.guardianPhoneNumber

        firestore.collection("guardian_request")
            .whereEqualTo("userPhone", userPhone)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FriendsViewModel", "User not found in Firestore")
                } else {
                    for (document in documents) {
                        val requestList = document.get("request") as? List<Map<String, Boolean>>
                        val existingGuardian =
                            requestList?.any { it.containsKey(thisPhoneUser) } == true

                        if (existingGuardian) {
                            // El guardián está en el array, eliminarlo
                            val guardianToRemove = mapOf(thisPhoneUser to false)
                            firestore.collection("guardian_request")
                                .document(document.id)
                                .update("request", FieldValue.arrayRemove(guardianToRemove))
                                .addOnSuccessListener {
                                    Log.d(
                                        "FriendsViewModel",
                                        "Guardian removed from existing document with ID: ${document.id}"
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w("FriendsViewModel", "Error updating document", e)
                                }
                        } else {
                            Log.d(
                                "FriendsViewModel",
                                "Guardian does not exist in the request array"
                            )
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("FriendsViewModel", "Error getting documents", e)
            }

        viewModelScope.launch {
            userDatabaseDaoRepositoryImp.deleteGuardian(userGuardiansContacts)
        }
    }

    var phoneNumber: String? = null // Declaración inicial de phoneNumber

    @SuppressLint("Range")
    fun readContactData(context: Context, contactUri: Uri) {
        val cursor = context.contentResolver.query(contactUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val name = it.getString(nameIndex)
                val photoUriStr =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI))
                var photoUri: Uri? = null

                if (photoUriStr != null) {
                    photoUri = Uri.parse(photoUriStr)
                    Log.d("ContactPicker", "Photo URI: $photoUri")
                } else {
                    Log.d("ContactPicker", "No photo found.")
                }

                Log.d("ContactPicker", "Contact Name: $name")

                // Obtener el número de teléfono
                val phoneCursor = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(contactId),
                    null
                )
                phoneCursor?.use { pc ->
                    if (pc.moveToFirst()) {
                        val phoneIndex =
                            pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        phoneNumber = pc.getString(phoneIndex)
                        Log.d("ContactPickerPhone", "Phone Number: $phoneNumber")
                    } else {
                        Log.d("ContactPickerPhone", "No phone number found.")
                    }
                }


                phoneNumber = editPhoneNumber(phoneNumber!!)

                // Agregar contacto como guardián
                if (!phoneNumber.isNullOrEmpty()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        userDatabaseDaoRepositoryImp.doesPhoneNumberExist(phoneNumber!!)
                            .let { exist ->
                                if (exist) {
                                    _showDuplicateDialog.value = phoneNumber
                                } else {
                                    Log.d("ContactPickerPhone", "Añadiendo contacto: $phoneNumber")
                                    addGuardian(
                                        UserGuardiansContacts(
                                            guardianPhoneNumber = phoneNumber!!,
                                            guardianName = name,
                                            guardianSurname = "",
                                            isGuardianRegister = false,
                                            isGuardianActive = false
                                        ),
                                        GuardianAlertLevel(
                                            userGuardianId = phoneNumber!!,
                                            low = false,
                                            medium = false,
                                            high = false,
                                            critical = false
                                        )
                                    )
                                }
                            }
                    }
                } else {
                    Log.d("ContactPickerPhone", "No phone number found 2.")
                }

            } else {
                Log.d("ContactPickerPhone", "No contact found 3.")
            }
        }
    }

    private fun editPhoneNumber(phoneNumber: String): String {
        val phoneNumberUtil = PhoneNumberUtil.getInstance()
        var formattedPhoneNumber = phoneNumber.replace(" ", "").trim()

        // Si el número no comienza con el prefijo internacional de España
        if (!formattedPhoneNumber.startsWith("+34")) {
            // Agrega el prefijo internacional de España
            formattedPhoneNumber = "+34$formattedPhoneNumber"
        }

        try {
            val parsedPhoneNumber = phoneNumberUtil.parse(formattedPhoneNumber, null)
            val countryCode = phoneNumberUtil.getRegionCodeForNumber(parsedPhoneNumber)

            // Si el código de país no es nulo, significa que se pudo identificar el país
            if (countryCode != null) {
                // Formatea el número de teléfono con el prefijo internacional del país
                formattedPhoneNumber = phoneNumberUtil.format(
                    parsedPhoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.E164
                )
                Log.d("ContactPickerPhone", "Phone Number2: $formattedPhoneNumber")

            } else {
                // Si no se puede identificar el país, se devuelve el número sin cambios
                formattedPhoneNumber = phoneNumberUtil.format(
                    parsedPhoneNumber,
                    PhoneNumberUtil.PhoneNumberFormat.E164
                )
                Log.d("ContactPickerPhone", "Phone Number2: $formattedPhoneNumber")

            }
        } catch (e: Exception) {
            // Manejar errores de análisis de números de teléfono
            e.printStackTrace()
        }

        return formattedPhoneNumber
    }


    // Método para mostrar el diálogo de confirmación de eliminación
    fun confirmDelete(contact: UserGuardiansContacts) {
        _contactToDelete.value = contact
    }

    // Método para cerrar el diálogo de confirmación de eliminación
    fun dismissDeleteDialog() {
        _contactToDelete.value = null
    }

    // Método para cerrar el diálogo de número duplicado
    fun dismissDuplicateDialog() {
        _showDuplicateDialog.value = null
    }

    fun getGuardianAlertLevel(guardianPhoneNumber: String) {
        viewModelScope.launch {
            userDatabaseDaoRepositoryImp.getAllAlertsOfGuardiansByPhone(guardianPhoneNumber)
                .collect { item ->
                    _guardianAlertLevel.value = item
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

    fun inviteToApp(guardianPhoneNumber: String, context: Context) {
        // Mandar SMS
        val smsText =
            "¡Hola! Soy tu amigo de la app de emergencias. Descárgala en Google Play: https://play.google.com/store/apps/details?id=com.fedeyruben.proyectofinaldamd"
        val smsUri = Uri.parse("smsto:$guardianPhoneNumber")
        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO, smsUri)
        intent.putExtra("sms_body", smsText)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun sendAlert(level: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val location = LocationUpdateService.currentLocation.value
                val phoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber

                if (location != null && phoneNumber != null) {
                    val alertData = hashMapOf(
                        "alertLevel" to level,
                        "geoPoint" to GeoPoint(location.latitude, location.longitude),
                        "isAlert" to true,
                        "phoneNumber" to phoneNumber
                    )

                    val alertsRef = firestore.collection("Alerts").document(phoneNumber)

                    alertsRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                // Actualiza el documento existente
                                alertsRef.update(alertData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Log.d("sendAlert", "Alerta actualizada: $alertData")
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(
                                            "sendAlert",
                                            "Error al actualizar la alerta: ${e.message}"
                                        )
                                        onFailure(e)
                                    }
                            } else {
                                // Crea un nuevo documento
                                alertsRef.set(alertData)
                                    .addOnSuccessListener {
                                        Log.d("sendAlert", "Alerta creada: $alertData")
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("sendAlert", "Error al crear la alerta: ${e.message}")
                                        onFailure(e)
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("sendAlert", "Error al obtener el documento: ${e.message}")
                            onFailure(e)
                        }
                } else {
                    onFailure(Exception("Location or phone number not available"))
                }
            } catch (e: Exception) {
                Log.e("sendAlert", "Error: ${e.message}")
                onFailure(e)
            }
        }
    }

    fun cancelAlert(alertLevel: String) {
        viewModelScope.launch {
            try {
                val userPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
                if (userPhoneNumber != null) {
                    val querySnapshot = firestore.collection("Alerts")
                        .whereEqualTo("phoneNumber", userPhoneNumber)
                        .get()
                        .await()

                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        firestore.collection("Alerts").document(document.id)
                            .update(
                                mapOf(
                                    "alertLevel" to null,
                                    "geoPoint" to GeoPoint(0.0, 0.0),
                                    "isAlert" to false
                                )
                            )
                            .addOnSuccessListener {
                                Log.d("cancelAlert", "Alerta cancelada con éxito.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("cancelAlert", "Error al cancelar la alerta: ${e.message}")
                            }
                    } else {
                        Log.e("cancelAlert", "No se encontró el documento con el número de teléfono: $userPhoneNumber")
                    }
                } else {
                    Log.e("cancelAlert", "Número de teléfono no disponible.")
                }
            } catch (e: Exception) {
                Log.e("cancelAlert", "Error: ${e.message}")
            }
        }
    }
}


