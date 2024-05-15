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

}