package com.fedeyruben.proyectofinaldamd.data.room

import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserDatabaseDaoRepositoryImp @Inject constructor(private val userDataBaseDao: UserDataBaseDaoRepository) :
    UserDataBaseDaoRepository {

    /** ZONA DE CONTACTOS/GUARDIANES **/
    override fun getAllGuardians(): Flow<List<UserGuardiansContacts>> {
        return userDataBaseDao.getAllGuardians().flowOn(Dispatchers.IO).conflate()
    }

    override fun getGuardianByPhoneNumber(phoneNumber: String): Flow<UserGuardiansContacts> {
        return userDataBaseDao.getGuardianByPhoneNumber(phoneNumber).flowOn(Dispatchers.IO)
            .conflate()
    }

    override suspend fun doesPhoneNumberExist(phoneNumber: String): Boolean {
        return userDataBaseDao.doesPhoneNumberExist(phoneNumber)
    }

    override suspend fun insertGuardian(userGuardiansContacts: UserGuardiansContacts) {
        userDataBaseDao.insertGuardian(userGuardiansContacts)
    }

    override suspend fun updateGuardian(userGuardiansContacts: UserGuardiansContacts) {
        userDataBaseDao.updateGuardian(userGuardiansContacts)
    }

    override suspend fun deleteGuardian(userGuardiansContacts: UserGuardiansContacts) {
        userDataBaseDao.deleteGuardian(userGuardiansContacts)
    }

    override suspend fun updateIsGuardianRegister(
        phoneNumber: String,
        isGuardianRegister: Boolean
    ) {
        return userDataBaseDao.updateIsGuardianRegister(phoneNumber, isGuardianRegister)
    }

    override suspend fun updateIsGuardianActive(phoneNumber: String, isGuardianActive: Boolean) {
        return userDataBaseDao.updateIsGuardianActive(phoneNumber, isGuardianActive)
    }

    override fun isGuardianActive(phoneNumber: String): Boolean {
        return userDataBaseDao.isGuardianActive(phoneNumber)
    }


    /** ZONA DE ALERTAS **/
    override fun getAllAlertsOfGuardians(): Flow<List<GuardianAlertLevel>> {
        return userDataBaseDao.getAllAlertsOfGuardians()
    }

    override fun getAllAlertsOfGuardiansByPhone(phoneNumber: String): Flow<GuardianAlertLevel> {
        return userDataBaseDao.getAllAlertsOfGuardiansByPhone(phoneNumber)
    }

    override suspend fun insertGuardianAlertLevel(guardianAlertLevel: GuardianAlertLevel) {
        userDataBaseDao.insertGuardianAlertLevel(guardianAlertLevel)
    }

    override suspend fun updateLowColumn(phoneNumber: String, newLowValue: Boolean) {
        userDataBaseDao.updateLowColumn(phoneNumber, newLowValue)
    }

    override suspend fun updateMediumColumn(phoneNumber: String, newMediumValue: Boolean) {
        userDataBaseDao.updateMediumColumn(phoneNumber, newMediumValue)
    }

    override suspend fun updateHighColumn(phoneNumber: String, newHighValue: Boolean) {
        userDataBaseDao.updateHighColumn(phoneNumber, newHighValue)
    }

    override suspend fun updateCriticalColumn(phoneNumber: String, newCriticalValue: Boolean) {
        userDataBaseDao.updateCriticalColumn(phoneNumber, newCriticalValue)
    }
}