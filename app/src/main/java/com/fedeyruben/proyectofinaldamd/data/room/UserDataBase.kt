package com.fedeyruben.proyectofinaldamd.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts

@Database(entities = [UserGuardiansContacts::class, GuardianAlertLevel::class], version = 1, exportSchema = false)
@TypeConverters(UriConverter::class)
abstract class UserDataBase: RoomDatabase() {
    abstract fun userDataBaseDao(): UserDataBaseDaoRepository
}