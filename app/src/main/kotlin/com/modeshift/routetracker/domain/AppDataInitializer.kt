package com.modeshift.routetracker.domain

import com.modeshift.routetracker.core.models.onFailure
import com.modeshift.routetracker.core.models.onSuccess
import com.modeshift.routetracker.di.annotations.AppScope
import com.modeshift.routetracker.domain.InitializationSate.Error
import com.modeshift.routetracker.domain.InitializationSate.Idle
import com.modeshift.routetracker.domain.InitializationSate.Initialized
import com.modeshift.routetracker.domain.InitializationSate.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataInitializer @Inject constructor(
    private val repository: RouteTrackerRepository,
    @AppScope
    private val appScope: CoroutineScope
) {
    private var routesCached: Boolean? = null
    private var stopsCached: Boolean? = null
    private val _state = MutableStateFlow<InitializationSate>(Idle)
    val state = _state.asStateFlow()

    fun initialize() = appScope.launch {
        _state.emit(Loading)
        routesCached = null
        stopsCached = null
        launch {
            repository.getRoutes()
                .onSuccess {
                    routesCached = it.data.isNotEmpty()
                    if (routesCached == true && stopsCached == true) {
                        _state.emit(Initialized)
                    } else if (stopsCached != null) {
                        emitError("Something went wrong")
                    }
                }.onFailure {
                    emitError(it.message)
                }
        }
        launch {
            repository.getStops()
                .onSuccess {
                    stopsCached = it.data.isNotEmpty()
                    if (routesCached == true && stopsCached == true) {
                        _state.emit(Initialized)
                    } else if (routesCached != null) {
                        emitError("Something went wrong")
                    }
                }.onFailure { emitError(it.message) }
        }
    }

    private suspend fun emitError(message: String) {
        _state.emit(Error(message))
        _state.emit(Idle)
    }
}

sealed interface InitializationSate {
    data object Idle : InitializationSate
    data object Loading : InitializationSate
    data object Initialized : InitializationSate
    data class Error(val message: String) : InitializationSate
}