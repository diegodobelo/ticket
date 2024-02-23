package com.diegodobelo.ticketmaster.models.transformers

import com.diegodobelo.ticketmaster.models.Event
import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.usecases.FormatDateUseCase
import javax.inject.Inject

class EventDataTransformer @Inject constructor(
    val formatDateUseCase: FormatDateUseCase
) {
    operator fun invoke(cityEvent: Event): EventData? {
        val imageUrl = cityEvent.images.getOrNull(0)?.url ?: ""
        val venue = cityEvent._embedded.venues.getOrNull(0)
        return if (venue == null) {
            null
        } else {
            val formattedDate = formatDateUseCase(cityEvent.dates.start.localDate)
            EventData(
                id = cityEvent.id,
                name = cityEvent.name,
                date = formattedDate,
                venue = venue.name,
                imageUrl = imageUrl,
                city = venue.city.name,
                state = venue.state.stateCode
            )
        }
    }
}