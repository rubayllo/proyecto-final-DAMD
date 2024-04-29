package com.fedeyruben.proyectofinaldamd.friends

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendsViewModel : ViewModel() {

    // Estado mutable que almacena la lista de amigos
    private val _friends = MutableLiveData<List<Friend>>(emptyList())
    val friends: LiveData<List<Friend>> = _friends

    fun addFriend(friend: Friend) {
        val currentList = _friends.value ?: emptyList()
        _friends.value = currentList + friend
    }

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
                        val phoneNumber = pc.getString(phoneIndex)
                        Log.d("ContactPicker", "Phone Number: $phoneNumber")
                    } else {
                        Log.d("ContactPicker", "No phone number found.")
                    }
                }

                // Agregar contacto como amigo
                addFriend(
                    Friend(
                        id = contactId.toInt(),
                        nombre = name,
                        apellido = "",
                        imageResId = photoUri
                    )
                )

            } else {
                Log.d("ContactPicker", "No contact found.")
            }
        }
    }
}