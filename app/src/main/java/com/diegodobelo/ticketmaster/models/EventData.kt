package com.diegodobelo.ticketmaster.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EventData(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "event_name") val name: String,
    @ColumnInfo(name = "event_date") val date: String,
    @ColumnInfo(name = "event_venue") val venue: String,
    @ColumnInfo(name = "event_image") val imageUrl: String,
    @ColumnInfo(name = "event_city") val city: String,
    @ColumnInfo(name = "event_state") val state: String
)