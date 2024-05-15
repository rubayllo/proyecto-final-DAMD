package com.fedeyruben.proyectofinaldamd.data.di

import android.content.Context
import androidx.room.Room
import com.fedeyruben.proyectofinaldamd.data.room.UserDataBase
import com.fedeyruben.proyectofinaldamd.data.room.UserDataBaseDaoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {
    @Singleton
    @Provides
    fun privideUserDataBaseDao(userDataBase: UserDataBase): UserDataBaseDaoRepository {
        return userDataBase.userDataBaseDao()
    }

    @Singleton
    @Provides
    fun provideUserDataBase(@ApplicationContext context: Context): UserDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            UserDataBase::class.java,
            "user_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
}