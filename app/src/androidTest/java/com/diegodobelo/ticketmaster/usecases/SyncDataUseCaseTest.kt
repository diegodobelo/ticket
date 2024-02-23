package com.diegodobelo.ticketmaster.usecases

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diegodobelo.ticketmaster.helper.createFakeCityEvent
import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.models.transformers.EventDataTransformer
import com.diegodobelo.ticketmaster.network.TicketMasterService
import com.diegodobelo.ticketmaster.storage.AppDatabase
import com.diegodobelo.ticketmaster.storage.dao.CityEventDao
import com.diegodobelo.ticketmaster.storage.repositories.CityEventRepository
import com.diegodobelo.ticketmaster.storage.repositories.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SyncDataUseCaseTest {

    private lateinit var subject: SyncDataUseCase
    private lateinit var ticketMasterService: TicketMasterService
    private lateinit var cityEventRepository: Repository<List<EventData>>
    private lateinit var eventDataTransformer: EventDataTransformer
    private lateinit var formatDateUseCase: FormatDateUseCase

    private lateinit var cityEventDao: CityEventDao

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        formatDateUseCase = FormatDateUseCase()
        eventDataTransformer = EventDataTransformer(formatDateUseCase)

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()

        cityEventDao = db.conversionsDao()

        ticketMasterService = mock()
        cityEventRepository = CityEventRepository(cityEventDao)

        subject = SyncDataUseCase(
            eventDataTransformer,
            ticketMasterService,
            cityEventRepository
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun checkSyncDataInsertsOnDb() = runTest {
        Mockito.`when`(ticketMasterService.getEvents("city")).thenReturn(createFakeCityEvent())

        val result = subject("city")
        assertTrue(result.isSuccess)

        assertEquals(1, cityEventDao.getAll().first().size)
    }

    @Test
    fun checkSyncDataUpdatesValueOnDb() = runTest {
        // Insert
        Mockito.`when`(ticketMasterService.getEvents("city")).thenReturn(createFakeCityEvent("name_1"))

        var result = subject("city")
        assertTrue(result.isSuccess)

        assertEquals(1, cityEventDao.getAll().first().size)
        var cityName = cityEventDao.getAll().first()[0].name
        assertEquals("city name", cityName, "name_1")

        // Update
        Mockito.`when`((ticketMasterService.getEvents("city"))).thenReturn(createFakeCityEvent("name_2"))

        result = subject("city")
        assertTrue(result.isSuccess)

        assertEquals(1, cityEventDao.getAll().first().size)
        cityName = cityEventDao.getAll().first()[0].name
        assertEquals("city name", cityName, "name_2")
    }

    @Test
    fun checkSyncDataAddsNewEntryOnDb() = runTest {
        // Insert
        Mockito.`when`(ticketMasterService.getEvents("city")).thenReturn(createFakeCityEvent(
            eventName = "name_1",
            id = "123"
        ))

        var result = subject("city")
        assertTrue(result.isSuccess)

        assertEquals(1, cityEventDao.getAll().first().size)

        // Add
        Mockito.`when`(ticketMasterService.getEvents("city")).thenReturn(createFakeCityEvent(
            eventName = "name_2",
            id = "456"
        ))

        result = subject("city")
        assertTrue(result.isSuccess)

        assertEquals(2, cityEventDao.getAll().first().size)
    }
}