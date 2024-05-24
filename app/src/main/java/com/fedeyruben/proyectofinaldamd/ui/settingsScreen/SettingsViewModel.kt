package com.fedeyruben.proyectofinaldamd.ui.settingsScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fedeyruben.proyectofinaldamd.data.room.UserDatabaseDaoRepositoryImp
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val userDatabaseDaoRepositoryImp: UserDatabaseDaoRepositoryImp): ViewModel(){
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
}