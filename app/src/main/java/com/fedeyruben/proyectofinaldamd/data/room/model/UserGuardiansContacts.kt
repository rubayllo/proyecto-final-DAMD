package com.fedeyruben.proyectofinaldamd.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserGuardiansContacts(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "guardian_phone_number")
    val guardianPhoneNumber: String,
    @ColumnInfo(name = "guardian_name")
    val guardianName: String,
    @ColumnInfo(name = "guadian_surname")
    val guardianSurname: String,
    @ColumnInfo(name = "guardian_image")
    val guardianImage: String,
    @ColumnInfo(name = "is_guardian_active")
    val isGuardianActive: Boolean,
    @ColumnInfo(name = "alert_level")
    val alertLevel: AlertLevel,
)
data class AlertLevel (
    @ColumnInfo(name = "low")
    val low: Boolean = false,
    @ColumnInfo(name = "medium")
    val medium: Boolean = false,
    @ColumnInfo(name = "high")
    val high: Boolean = false,
    @ColumnInfo(name = "critical")
    val critical: Boolean = false,
)
