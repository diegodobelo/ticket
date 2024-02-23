package com.diegodobelo.ticketmaster.helper

import com.diegodobelo.ticketmaster.models.City
import com.diegodobelo.ticketmaster.models.CityEvent
import com.diegodobelo.ticketmaster.models.DateDetail
import com.diegodobelo.ticketmaster.models.Embedded
import com.diegodobelo.ticketmaster.models.Event
import com.diegodobelo.ticketmaster.models.EventImage
import com.diegodobelo.ticketmaster.models.LocalDate
import com.diegodobelo.ticketmaster.models.LocationInfo
import com.diegodobelo.ticketmaster.models.StateCode
import com.diegodobelo.ticketmaster.models.Venue

fun createFakeCityEvent(
    eventName: String = "event_name",
    id: String = "123"
) = CityEvent(
    Embedded(
        listOf(
            Event(
                id = id,
                images = listOf(
                    EventImage(
                        url = "image_url"
                    )
                ),
                name = eventName,
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