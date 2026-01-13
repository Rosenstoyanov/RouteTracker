package com.modeshift.routetracker.location

import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.location.LocationServiceState.Stopped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationServiceStateEmitter @Inject constructor(
    @AppScope
    private val appScope: CoroutineScope
) {
    private val _state = MutableStateFlow<LocationServiceState>(Stopped)
    val state = _state.asStateFlow()

    fun updateState(state: LocationServiceState) = appScope.launch {
        _state.emit(state)
    }
}

sealed interface LocationServiceState {
    data object Running : LocationServiceState
    data object Stopped : LocationServiceState
}