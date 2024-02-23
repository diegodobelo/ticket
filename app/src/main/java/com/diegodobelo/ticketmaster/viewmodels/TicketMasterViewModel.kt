package com.diegodobelo.ticketmaster.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodobelo.ticketmaster.di.IoDispatcher
import com.diegodobelo.ticketmaster.models.EventData
import com.diegodobelo.ticketmaster.usecases.GetEventsByCityUseCase
import com.diegodobelo.ticketmaster.usecases.SyncDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TicketMasterViewModel @Inject constructor(
    private val getEventsByCityUseCase: GetEventsByCityUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _cityInput = MutableStateFlow("")
    val cityInput = _cityInput.asStateFlow()

    val cityEventState: StateFlow<CityEventState> = _cityInput.flatMapLatest { city ->
        getEventsByCityUseCase(city)
            .map {
                if (it.isEmpty()) {
                    CityEventState.Empty
                } else {
                    CityEventState.Loaded(it)
                }
            }
    }.stateIn(
        initialValue = CityEventState.Loading,
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_GRACE_PERIOD)
    )

    init {
        viewModelScope.launch {
            _cityInput
                .debounce(DEBOUCE)
                .distinctUntilChanged()
                .flowOn(ioDispatcher)
                .collect {
                    if (it.isNotEmpty()) {
                        syncDataUseCase(it)
                    }
                }
        }

    }

    fun searchEvents(city: String) {
        _cityInput.value = city
    }

    companion object {
        const val DEBOUCE = 800L
        const val SUBSCRIPTION_GRACE_PERIOD = 5000L
    }
}

sealed class CityEventState {
    object Empty : CityEventState()
    object Loading : CityEventState()
    class Loaded(val events: List<EventData>) : CityEventState()
}