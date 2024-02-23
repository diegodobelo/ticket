package com.diegodobelo.ticketmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.diegodobelo.ticketmaster.ui.composables.CenterContent
import com.diegodobelo.ticketmaster.ui.composables.CityInput
import com.diegodobelo.ticketmaster.ui.composables.EventCard
import com.diegodobelo.ticketmaster.ui.theme.AppTheme
import com.diegodobelo.ticketmaster.viewmodels.CityEventState
import com.diegodobelo.ticketmaster.viewmodels.TicketMasterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val ticketMasterViewModel: TicketMasterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                EventsPage()
            }
        }
    }

    @Composable
    fun EventsPage() {
        Column {
            val cityInput by ticketMasterViewModel.cityInput.collectAsState()
            CityInput(
                currentCity = cityInput,
                onValueChanged = {
                    ticketMasterViewModel.searchEvents(it)
                }
            )
            EventsList()
        }

    }

    @Composable
    fun EventsList() {
        val events by ticketMasterViewModel.cityEventState.collectAsState("")
        when(events) {
            CityEventState.Empty -> {
                CenterContent {
                    Text(text = stringResource(id = R.string.no_content_found))
                }
            }

            CityEventState.Loading -> {
                CenterContent {
                    CircularProgressIndicator(
                        modifier = Modifier.width(48.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }

            is CityEventState.Loaded -> {
                val cityEvents = (events as CityEventState.Loaded).events
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cityEvents, key = { it.id }) {
                        EventCard(eventData = it)
                    }
                }
            }
        }
    }
}
