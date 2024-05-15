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

    override fun getGuardianById(id: Long): Flow<UserGuardiansContacts> {
        return userDataBaseDao.getGuardianById(id).flowOn(Dispatchers.IO).conflate()
    }

    override fun getGuardianByPhoneNumber(phoneNumber: String): Flow<UserGuardiansContacts> {
        return userDataBaseDao.getGuardianByPhoneNumber(phoneNumber).flowOn(Dispatchers.IO)
            .conflate()
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


    /** ZONA DE ALERTAS **/
    override fun getAllAlertsOfGuardians(): Flow<List<GuardianAlertLevel>> {
        return userDataBaseDao.getAllAlertsOfGuardians()
    }

    override fun getAllAlertsOfGuardiansById(id: Long): Flow<GuardianAlertLevel> {
        return userDataBaseDao.getAllAlertsOfGuardiansById(id)
    }

    override suspend fun updateLowColumn(userGuardianId: Long, newLowValue: Boolean) {
        userDataBaseDao.updateLowColumn(userGuardianId, newLowValue)
    }

    override suspend fun updateMediumColumn(userGuardianId: Long, newMediumValue: Boolean) {
        userDataBaseDao.updateMediumColumn(userGuardianId, newMediumValue)
    }

    override suspend fun updateHighColumn(userGuardianId: Long, newHighValue: Boolean) {
        userDataBaseDao.updateHighColumn(userGuardianId, newHighValue)
    }

    override suspend fun updateCriticalColumn(userGuardianId: Long, newCriticalValue: Boolean) {
        userDataBaseDao.updateCriticalColumn(userGuardianId, newCriticalValue)
    }
}