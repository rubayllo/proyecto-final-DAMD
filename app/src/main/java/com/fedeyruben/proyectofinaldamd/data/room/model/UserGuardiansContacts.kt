package com.fedeyruben.proyectofinaldamd.data.room.model

import android.net.Uri
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
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Clave primaria que se generará automáticamente al insertar una nueva fila en la tabla

    @ColumnInfo(name = "guardian_phone_number")
    val guardianPhoneNumber: String, // Número de teléfono del guardián

    @ColumnInfo(name = "guardian_name")
    val guardianName: String, // Nombre del guardián

    @ColumnInfo(name = "guadian_surname")
    val guardianSurname: String? = null, // Apellido del guardián

    @ColumnInfo(name = "guardian_image")
    val guardianImage: Uri? = null, // Ruta de la imagen del guardián

    @ColumnInfo(name = "is_guardian_register")
    val isGuardianRegister: Boolean? = false, // Indica si el guardián está registrado en la app o no

    @ColumnInfo(name = "is_guardian_active")
    val isGuardianActive: Boolean? = false, // Indica si el guardián a aceptado ser guardián del usuario
)

@Entity(tableName = "guardian_alert_level", foreignKeys = [ForeignKey(entity = UserGuardiansContacts::class, parentColumns = ["id"], childColumns = ["user_guardian_id"], onDelete = ForeignKey.CASCADE)])
data class GuardianAlertLevel(
    @PrimaryKey
    @ColumnInfo(name = "user_guardian_id")
    val userGuardianId: Long, // Clave primaria de UserGuardiansContacts

    @ColumnInfo(name = "low")
    val low: Boolean? = false, // Indica si el nivel de alerta es bajo

    @ColumnInfo(name = "medium")
    val medium: Boolean? = false, // Indica si el nivel de alerta es medio

    @ColumnInfo(name = "high")
    val high: Boolean? = false, // Indica si el nivel de alerta es alto

    @ColumnInfo(name = "critical")
    val critical: Boolean? = false // Indica si el nivel de alerta es crítico
)

