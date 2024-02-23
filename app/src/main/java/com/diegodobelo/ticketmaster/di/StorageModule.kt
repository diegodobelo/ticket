package com.diegodobelo.ticketmaster.di

import android.content.Context
import androidx.room.Room
import com.diegodobelo.ticketmaster.storage.AppDatabase
import com.diegodobelo.ticketmaster.storage.dao.CityEventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(appContext, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .build()
    }

    @Provides
    fun provideLatestCityEventDao(db: AppDatabase): CityEventDao {
        return db.conversionsDao()
    }
}