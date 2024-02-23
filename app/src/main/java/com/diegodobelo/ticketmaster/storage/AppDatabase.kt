package com.diegodobelo.ticketmaster.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.storage.dao.CityEventDao

@Database(
    entities = [EventData::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()  {
    abstract fun conversionsDao(): CityEventDao

    companion object {
        const val DATABASE_NAME = "city_event_db"
    }
}