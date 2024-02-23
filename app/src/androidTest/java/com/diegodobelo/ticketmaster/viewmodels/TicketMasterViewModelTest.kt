package com.diegodobelo.ticketmaster.viewmodels

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.diegodobelo.ticketmaster.helper.createFakeCityEvent
import com.diegodobelo.ticketmaster.models.transformers.EventDataTransformer
import com.diegodobelo.ticketmaster.network.TicketMasterService
import com.diegodobelo.ticketmaster.storage.AppDatabase
import com.diegodobelo.ticketmaster.storage.dao.CityEventDao
import com.diegodobelo.ticketmaster.storage.repositories.CityEventRepository
import com.diegodobelo.ticketmaster.usecases.FormatDateUseCase
import com.diegodobelo.ticketmaster.usecases.GetEventsByCityUseCase
import com.diegodobelo.ticketmaster.usecases.SyncDataUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class TicketMasterViewModelTest {

    private lateinit var subject: TicketMasterViewModel
    private lateinit var syncDataUseCase: SyncDataUseCase
    private lateinit var ticketMasterService: TicketMasterService
    private lateinit var cityEventRepository: CityEventRepository
    private lateinit var eventDataTransformer: EventDataTransformer
    private lateinit var formatDateUseCase: FormatDateUseCase
    private lateinit var getEventsByCityUseCase: GetEventsByCityUseCase

    private lateinit var cityEventDao: CityEventDao

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        formatDateUseCase = FormatDateUseCase()
        eventDataTransformer = EventDataTransformer(formatDateUseCase)

        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()

        cityEventDao = db.conversionsDao()

        ticketMasterService = Mockito.mock()
        cityEventRepository = CityEventRepository(cityEventDao)
        getEventsByCityUseCase = GetEventsByCityUseCase(cityEventRepository)

        syncDataUseCase = SyncDataUseCase(
            eventDataTransformer,
            ticketMasterService,
            cityEventRepository
        )

        subject = TicketMasterViewModel(
            getEventsByCityUseCase,
            syncDataUseCase,
            StandardTestDispatcher()
        )
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun checkSyncDataUpdatesViewModelEventsState() = runTest {
        Mockito.`when`(ticketMasterService.getEvents("Boston")).thenReturn(createFakeCityEvent())
        advanceUntilIdle()

        assertTrue(subject.cityEventState.value is CityEventState.Loading)
        subject.searchEvents("Boston")
        syncDataUseCase("Boston")
        val firstItem = subject.cityInput.first()
        assertEquals("Boston", firstItem)

        val firstEvent = subject.cityEventState.drop(1).first()
        assertTrue(firstEvent is CityEventState.Loaded)
        val loadedEvents = (subject.cityEventState.value as CityEventState.Loaded)
        assertEquals(1, loadedEvents.events.size)
    }

    @Test
    fun checkSyncDataUpdatesViewModelEventStateEmpty() = runTest {
        Mockito.`when`(ticketMasterService.getEvents("Boston")).thenReturn(createFakeCityEvent())
        advanceUntilIdle()

        assertTrue(subject.cityEventState.value is CityEventState.Loading)
        subject.searchEvents("Fresno")
        syncDataUseCase("Fresno")
        val firstItem = subject.cityInput.first()
        assertEquals("Fresno", firstItem)

        val firstEvent = subject.cityEventState.drop(1).first()
        assertTrue(firstEvent is CityEventState.Empty)
    }

}