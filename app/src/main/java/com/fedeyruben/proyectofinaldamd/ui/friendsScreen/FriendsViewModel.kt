package com.fedeyruben.proyectofinaldamd.friends

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedeyruben.proyectofinaldamd.data.Friend
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
class FriendsViewModel @Inject constructor(private val userDatabaseDaoRepositoryImp: UserDatabaseDaoRepositoryImp) :
    ViewModel() {

    // Estado para el diálogo
    private val _showDuplicateDialog = MutableStateFlow<String?>(null)
    val showDuplicateDialog: StateFlow<String?> = _showDuplicateDialog

    // Estado para el diálogo de confirmación de eliminación
    private val _contactToDelete = MutableStateFlow<UserGuardiansContacts?>(null)
    val contactToDelete: StateFlow<UserGuardiansContacts?> = _contactToDelete

    // Estado mutable que almacena la lista de amigos
    private val _friends = MutableLiveData<List<Friend>>(emptyList())
    val friends: LiveData<List<Friend>> = _friends

    // Observa los contactos almacenados en Room
    private val _userGuardiansContactsList =
        MutableStateFlow<List<UserGuardiansContacts>>(emptyList())
    val userGuardiansContactsList = _userGuardiansContactsList.asStateFlow()

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
                        userDatabaseDaoRepositoryImp.doesPhoneNumberExist(phoneNumber!!).let { exist ->
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
}