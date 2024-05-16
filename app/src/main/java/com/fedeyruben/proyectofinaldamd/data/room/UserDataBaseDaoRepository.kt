package com.fedeyruben.proyectofinaldamd.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import kotlinx.coroutines.flow.Flow


// Interfaz que define las operaciones que se pueden realizar sobre la base de datos de usuarios
// Interfaz -> Repositorio -> ViewModel -> View

@Dao // Data Access Observer
interface UserDataBaseDaoRepository {

    /** TABLA UserGuardiansContacts **/
    @Query("SELECT * FROM user_guardians_contacts")
    fun getAllGuardians(): Flow<List<UserGuardiansContacts>>
    @Query("SELECT * FROM user_guardians_contacts WHERE guardian_phone_number = :phoneNumber")
    fun getGuardianByPhoneNumber(phoneNumber: String): Flow<UserGuardiansContacts>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardian(userGuardiansContacts: UserGuardiansContacts)
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGuardian(userGuardiansContacts: UserGuardiansContacts)
    @Delete
    suspend fun deleteGuardian(userGuardiansContacts: UserGuardiansContacts)

    /** TABLA GuardianAlertLevel **/
    @Query("SELECT * FROM guardian_alert_level")
    fun getAllAlertsOfGuardians(): Flow<List<GuardianAlertLevel>>
    @Query("SELECT * FROM guardian_alert_level WHERE guardian_phone_number_alert_level = :phoneNumber")
    fun getAllAlertsOfGuardiansByPhone(phoneNumber: String): Flow<GuardianAlertLevel>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardianAlertLevel(guardianAlertLevel: GuardianAlertLevel)
    @Query("UPDATE guardian_alert_level SET low = :newLowValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateLowColumn(phoneNumber: String, newLowValue: Boolean)
    @Query("UPDATE guardian_alert_level SET medium = :newMediumValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateMediumColumn(phoneNumber: String, newMediumValue: Boolean)
    @Query("UPDATE guardian_alert_level SET high = :newHighValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateHighColumn(phoneNumber: String, newHighValue: Boolean)
    @Query("UPDATE guardian_alert_level SET critical = :newCriticalValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateCriticalColumn(phoneNumber: String, newCriticalValue: Boolean)

}