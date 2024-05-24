package com.fedeyruben.proyectofinaldamd.friends

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
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FriendsViewModel @Inject constructor(private val userDatabaseDaoRepositoryImp: UserDatabaseDaoRepositoryImp):
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

    private fun addGuardian(
        userGuardiansContacts: UserGuardiansContacts,
        guardianAlertLevel: GuardianAlertLevel
    ) {
        viewModelScope.launch {
            userDatabaseDaoRepositoryImp.insertGuardian(userGuardiansContacts)
            userDatabaseDaoRepositoryImp.insertGuardianAlertLevel(guardianAlertLevel)
        }
    }

    fun deleteGuardian(userGuardiansContacts: UserGuardiansContacts) {
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
                                            guardianImage = photoUri,
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

    fun updateLowColumn(phoneNumber: String, level: Int, newValue: Boolean) {
        viewModelScope.launch {
            when(level) {
                1 -> userDatabaseDaoRepositoryImp.updateLowColumn(phoneNumber, newValue)
                2 -> userDatabaseDaoRepositoryImp.updateMediumColumn(phoneNumber, newValue)
                3 -> userDatabaseDaoRepositoryImp.updateHighColumn(phoneNumber, newValue)
                4 -> userDatabaseDaoRepositoryImp.updateCriticalColumn(phoneNumber, newValue)
            }
        }
    }
}