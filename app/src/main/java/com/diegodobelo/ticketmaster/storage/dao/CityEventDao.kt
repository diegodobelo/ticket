package com.diegodobelo.ticketmaster.storage.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodobelo.ticketmaster.models.EventData
import kotlinx.coroutines.flow.Flow

@Dao
interface CityEventDao {

    @Query("SELECT * FROM eventdata")
    fun getAll(): Flow<List<EventData>>

    @Query("SELECT * FROM eventdata WHERE event_city LIKE '%' || :city || '%'")
    fun getByCity(city: String): Flow<List<EventData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conversions: List<EventData>)

}