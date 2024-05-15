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
    @Query("SELECT * FROM user_guardians_contacts WHERE id = :id")
    fun getGuardianById(id: Long): Flow<UserGuardiansContacts>
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
    @Query("SELECT * FROM guardian_alert_level WHERE user_guardian_id = :id")
    fun getAllAlertsOfGuardiansById(id: Long): Flow<GuardianAlertLevel>

    @Query("UPDATE guardian_alert_level SET low = :newLowValue WHERE user_guardian_id = :userGuardianId")
    suspend fun updateLowColumn(userGuardianId: Long, newLowValue: Boolean)
    @Query("UPDATE guardian_alert_level SET medium = :newMediumValue WHERE user_guardian_id = :userGuardianId")
    suspend fun updateMediumColumn(userGuardianId: Long, newMediumValue: Boolean)
    @Query("UPDATE guardian_alert_level SET high = :newHighValue WHERE user_guardian_id = :userGuardianId")
    suspend fun updateHighColumn(userGuardianId: Long, newHighValue: Boolean)
    @Query("UPDATE guardian_alert_level SET critical = :newCriticalValue WHERE user_guardian_id = :userGuardianId")
    suspend fun updateCriticalColumn(userGuardianId: Long, newCriticalValue: Boolean)

}