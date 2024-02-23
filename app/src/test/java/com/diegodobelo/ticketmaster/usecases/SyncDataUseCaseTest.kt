package com.diegodobelo.ticketmaster.usecases

import com.diegodobelo.ticketmaster.models.City
import com.diegodobelo.ticketmaster.models.CityEvent
import com.diegodobelo.ticketmaster.models.DateDetail
import com.diegodobelo.ticketmaster.models.Embedded
import com.diegodobelo.ticketmaster.models.Event
import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.models.EventImage
import com.diegodobelo.ticketmaster.models.LocalDate
import com.diegodobelo.ticketmaster.models.LocationInfo
import com.diegodobelo.ticketmaster.models.StateCode
import com.diegodobelo.ticketmaster.models.Venue
import com.diegodobelo.ticketmaster.models.transformers.EventDataTransformer
import com.diegodobelo.ticketmaster.network.TicketMasterService
import com.diegodobelo.ticketmaster.storage.repositories.Repository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class SyncDataUseCaseTest {

    private lateinit var subject: SyncDataUseCase
    private lateinit var ticketMasterService: TicketMasterService
    private lateinit var cityEventRepository: Repository<List<EventData>>
    private lateinit var eventDataTransformer: EventDataTransformer
    private lateinit var formatDateUseCase: FormatDateUseCase

    @Before
    fun setup() {

        ticketMasterService = mock()
        cityEventRepository = mock()
        formatDateUseCase = FormatDateUseCase()
        eventDataTransformer = EventDataTransformer(formatDateUseCase)

        subject = SyncDataUseCase(
            eventDataTransformer,
            ticketMasterService,
            cityEventRepository
        )

    }

    @Test
    fun `check usecase returns success when data is available`() = runTest {
        whenever(ticketMasterService.getEvents(any())).thenReturn(createFakeCityEvent())
        whenever(cityEventRepository.insertAll(any())).then {  }

        val result = subject("city")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `check usecase returns failure when there is an exception on service`() = runTest {
        whenever(ticketMasterService.getEvents(any())).thenThrow(RuntimeException("Cannot get events"))

        val result = subject("city")
        assertTrue(result.isFailure)
        assertEquals("Cannot get events", result.exceptionOrNull()!!.message)
    }

    @Test
    fun `check usecase returns failure when there is an exception on db`() = runTest {
        whenever(ticketMasterService.getEvents(any())).thenReturn(createFakeCityEvent())
        whenever(cityEventRepository.insertAll(any())).thenThrow(RuntimeException("Cannot insertOnDB"))

        val result = subject("city")
        assertTrue(result.isFailure)
        assertEquals("Cannot insertOnDB", result.exceptionOrNull()!!.message)
    }

    private fun createFakeCityEvent() = CityEvent(
        Embedded(
            listOf(
                Event(
                    id = "123",
                    images = listOf(
                        EventImage(
                            url = "image_url"
                        )
                    ),
                    name = "event_name",
                    dates = DateDetail(
                        start = LocalDate(
                            "22-12-2024"
                        )
                    ),
                    _embedded = LocationInfo(
                        venues = listOf(
                            Venue(
                                name = "venue_name",
                                city = City(
                                    "Boston"
                                ),
                                state = StateCode(
                                    "MA"
                                )
                            )
                        )
                    )
                )
            )
        ),
    )

}