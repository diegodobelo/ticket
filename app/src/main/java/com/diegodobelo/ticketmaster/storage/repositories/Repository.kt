package com.diegodobelo.ticketmaster.storage.repositories

import kotlinx.coroutines.flow.Flow

interface Repository<T> {
    fun getAll() : Flow<T>
    suspend fun insertAll(value: T)
}