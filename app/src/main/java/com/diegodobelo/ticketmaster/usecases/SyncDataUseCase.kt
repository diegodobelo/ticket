package com.diegodobelo.ticketmaster.usecases

import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.models.transformers.EventDataTransformer
import com.diegodobelo.ticketmaster.network.TicketMasterService
import com.diegodobelo.ticketmaster.storage.repositories.Repository
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val eventDataTransformer: EventDataTransformer,
    private val ticketMasterService: TicketMasterService,
    private val cityEventRepository: Repository<List<EventData>>
) {

    suspend operator fun invoke(city: String) : Result<Unit> {
        try {
            val events = ticketMasterService.getEvents(city)._embedded.events.mapNotNull {
                eventDataTransformer(it)
            }
            cityEventRepository.insertAll(events)
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(Unit)
    }

}