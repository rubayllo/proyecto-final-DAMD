package com.fedeyruben.proyectofinaldamd.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fedeyruben.proyectofinaldamd.data.room.model.GuardianAlertLevel
import com.fedeyruben.proyectofinaldamd.data.room.model.UserGuardiansContacts
import com.fedeyruben.proyectofinaldamd.data.room.model.UserProtected

@Database(entities = [UserGuardiansContacts::class, GuardianAlertLevel::class, UserProtected::class], version = 1, exportSchema = false)
@TypeConverters(UriConverter::class)
abstract class UserDataBase: RoomDatabase() {
    abstract fun userDataBaseDao(): UserDataBaseDaoRepository
}