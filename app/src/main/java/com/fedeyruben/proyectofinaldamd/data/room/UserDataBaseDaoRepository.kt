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

    // Obtiene todos los contactos de la tabla
    @Query("SELECT * FROM user_guardians_contacts")
    fun getAllGuardians(): Flow<List<UserGuardiansContacts>>

    // Obtiene un contacto por su número de teléfono
    @Query("SELECT * FROM user_guardians_contacts WHERE guardian_phone_number = :phoneNumber")
    fun getGuardianByPhoneNumber(phoneNumber: String): Flow<UserGuardiansContacts>

    // Comprueba si un número de teléfono ya existe en la base de datos
    @Query("SELECT COUNT(*) > 0 FROM user_guardians_contacts WHERE guardian_phone_number = :phoneNumber")
    suspend fun doesPhoneNumberExist(phoneNumber: String): Boolean

    // Inserta un contacto en la tabla
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardian(userGuardiansContacts: UserGuardiansContacts)

    // Actualiza un contacto en la tabla
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGuardian(userGuardiansContacts: UserGuardiansContacts)

    // Elimina un contacto de la tabla
    @Delete
    suspend fun deleteGuardian(userGuardiansContacts: UserGuardiansContacts)

    // Actualiza is_guardian_register de un guardián en la tabla
    @Query("UPDATE user_guardians_contacts SET is_guardian_register = :isGuardianRegister WHERE guardian_phone_number = :phoneNumber")
    suspend fun updateIsGuardianRegister(phoneNumber: String, isGuardianRegister: Boolean)

    // Actualiza is_guardian_active de un guardián en la tabla
    @Query("UPDATE user_guardians_contacts SET is_guardian_active = :isGuardianActive WHERE guardian_phone_number = :phoneNumber")
    suspend fun updateIsGuardianActive(phoneNumber: String, isGuardianActive: Boolean)

    // Comprueba si el guardian esat acitvo
    @Query("SELECT is_guardian_active FROM user_guardians_contacts WHERE guardian_phone_number = :phoneNumber")
    fun isGuardianActive(phoneNumber: String): Boolean

    /** TABLA GuardianAlertLevel **/

    // Obtiene todas las alertas de los guardianes
    @Query("SELECT * FROM guardian_alert_level")
    fun getAllAlertsOfGuardians(): Flow<List<GuardianAlertLevel>>

    // Obtiene todas las alertas de un guardián por su número de teléfono
    @Query("SELECT * FROM guardian_alert_level WHERE guardian_phone_number_alert_level = :phoneNumber")
    fun getAllAlertsOfGuardiansByPhone(phoneNumber: String): Flow<GuardianAlertLevel>

    // Inserta una alerta de un guardián en la tabla
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuardianAlertLevel(guardianAlertLevel: GuardianAlertLevel)

    // Actualiza la columna low de un guardián en la tabla
    @Query("UPDATE guardian_alert_level SET low = :newLowValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateLowColumn(phoneNumber: String, newLowValue: Boolean)

    // Actualiza la columna medium de un guardián en la tabla
    @Query("UPDATE guardian_alert_level SET medium = :newMediumValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateMediumColumn(phoneNumber: String, newMediumValue: Boolean)

    // Actualiza la columna high de un guardián en la tabla
    @Query("UPDATE guardian_alert_level SET high = :newHighValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateHighColumn(phoneNumber: String, newHighValue: Boolean)

    // Actualiza la columna critical de un guardián en la tabla
    @Query("UPDATE guardian_alert_level SET critical = :newCriticalValue WHERE guardian_phone_number_alert_level = :phoneNumber")
    suspend fun updateCriticalColumn(phoneNumber: String, newCriticalValue: Boolean)

}