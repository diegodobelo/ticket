package com.diegodobelo.ticketmaster.network

import com.diegodobelo.ticketmaster.models.CityEvent
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketMasterService {

    @GET("events.json")
    suspend fun getEvents(@Query("city") city: String): CityEvent
}