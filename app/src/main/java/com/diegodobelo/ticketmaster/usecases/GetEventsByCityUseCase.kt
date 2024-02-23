package com.diegodobelo.ticketmaster.usecases

import com.diegodobelo.ticketmaster.storage.repositories.CityEventRepository
import javax.inject.Inject

class GetEventsByCityUseCase @Inject constructor(
    private val repository: CityEventRepository
) {

    operator fun invoke(city: String) = repository.getByCity(city)
}