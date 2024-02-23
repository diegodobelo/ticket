package com.diegodobelo.ticketmaster.di

import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.storage.dao.CityEventDao
import com.diegodobelo.ticketmaster.storage.repositories.CityEventRepository
import com.diegodobelo.ticketmaster.storage.repositories.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCityEventRepository(cityEventDao: CityEventDao): Repository<List<EventData>> {
        return CityEventRepository(cityEventDao)
    }
}