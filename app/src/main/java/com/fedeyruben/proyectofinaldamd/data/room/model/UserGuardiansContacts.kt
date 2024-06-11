package com.fedeyruben.proyectofinaldamd.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/*******************
 *  Todos los campos nulos no se pueden insertar en la base de datos
 *  hasta que sean configurados por el usuario después de agregar el guardián,
 *  por lo que inicialmente se les asigna un valor por defecto
 *  para que no sean nulos.
 *******************/


@Entity(tableName = "user_guardians_contacts")
data class UserGuardiansContacts(
    @PrimaryKey
    @ColumnInfo(name = "guardian_phone_number")
    val guardianPhoneNumber: String, // Número de teléfono del guardián

    @ColumnInfo(name = "guardian_name")
    val guardianName: String, // Nombre del guardián

    @ColumnInfo(name = "guadian_surname")
    val guardianSurname: String?, // Apellido del guardián

    @ColumnInfo(name = "is_guardian_register")
    val isGuardianRegister: Boolean, // Indica si el guardián está registrado en la app o no

    @ColumnInfo(name = "is_guardian_active")
    val isGuardianActive: Boolean, // Indica si el guardián a aceptado ser guardián del usuario
)

@Entity(tableName = "guardian_alert_level", foreignKeys = [ForeignKey(entity = UserGuardiansContacts::class, parentColumns = ["guardian_phone_number"], childColumns = ["guardian_phone_number_alert_level"], onDelete = ForeignKey.CASCADE)])
data class GuardianAlertLevel(
    @PrimaryKey
    @ColumnInfo(name = "guardian_phone_number_alert_level")
    val userGuardianId: String, // Clave primaria de UserGuardiansContacts

    @ColumnInfo(name = "low")
    val low: Boolean, // Indica si el nivel de alerta es bajo

    @ColumnInfo(name = "medium")
    val medium: Boolean, // Indica si el nivel de alerta es medio

    @ColumnInfo(name = "high")
    val high: Boolean, // Indica si el nivel de alerta es alto

    @ColumnInfo(name = "critical")
    val critical: Boolean // Indica si el nivel de alerta es crítico
)




