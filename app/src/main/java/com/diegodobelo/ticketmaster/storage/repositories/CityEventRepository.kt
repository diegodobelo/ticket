package com.diegodobelo.ticketmaster.storage.repositories

import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.storage.dao.CityEventDao
import javax.inject.Inject

class CityEventRepository @Inject constructor(
    private val cityEventDao: CityEventDao
) : Repository<List<EventData>> {

    fun getByCity(city: String) = cityEventDao.getByCity(city)

    override fun getAll() = cityEventDao.getAll()

    override suspend fun insertAll(value: List<EventData>) {
        cityEventDao.insertAll(value)
    }

}