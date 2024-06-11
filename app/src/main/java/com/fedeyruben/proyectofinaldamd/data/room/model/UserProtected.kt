package com.fedeyruben.proyectofinaldamd.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_request_protected")
data class UserProtected(
    @PrimaryKey
    @ColumnInfo(name = "user_phone_protected")
    val userPhoneProtected: String,

    @ColumnInfo(name = "isProtected")
    val isProtected: Boolean
)