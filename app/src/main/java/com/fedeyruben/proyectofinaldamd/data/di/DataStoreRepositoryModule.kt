package com.fedeyruben.proyectofinaldamd.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.fedeyruben.proyectofinaldamd.data.dataStore.repository.DataStoreRepository
import com.fedeyruben.proyectofinaldamd.data.dataStore.repository.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Aquí vais a tener que añadir todos los repositorios
 * que creeis de DataStore para poder inyectarlos en los VM
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreRepositoryModule {

    @Singleton
    @Provides
    fun providesDataStoreRepository(dataStore: DataStore<Preferences>): DataStoreRepository =
        DataStoreRepositoryImpl(dataStore)
}