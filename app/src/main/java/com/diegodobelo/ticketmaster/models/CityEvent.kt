package com.diegodobelo.ticketmaster.models

data class CityEvent(
    val _embedded: Embedded
)

data class Embedded(
    val events: List<Event>
)

data class Page(
    val number: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)

data class Event(
    val id: String,
    val images: List<EventImage>,
    val name: String,
    val dates: DateDetail,
    val _embedded: LocationInfo
)

data class LocationInfo(
    val venues: List<Venue>
)

data class Venue(
    val name: String,
    val city: City,
    val state: StateCode,
)

data class City(
    val name: String
)

data class StateCode(
    val stateCode: String
)

data class DateDetail(
    val start: LocalDate
)

data class LocalDate(
    val localDate: String
)

data class EventImage(
    val url: String
)