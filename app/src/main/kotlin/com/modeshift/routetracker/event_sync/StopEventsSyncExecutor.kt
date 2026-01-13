package com.modeshift.routetracker.event_sync

import com.modeshift.routetracker.core.models.onFailure
import com.modeshift.routetracker.core.network.status.NetworkConnectionMonitor
import com.modeshift.routetracker.core.network.status.NetworkConnectionStatus
import com.modeshift.routetracker.di.CoroutinesDispatcherProvider
import com.modeshift.routetracker.domain.RouteTrackerRepository
import com.modeshift.routetracker.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class StopEventsSyncExecutor @Inject constructor(
    private val repository: RouteTrackerRepository,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val networkConnectionMonitor: NetworkConnectionMonitor
) {
    private val mutex = Mutex()
    private var syncJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun ensureRunning(scope: CoroutineScope) {
        scope.launch {
            mutex.withLock {
                if (syncJob?.isActive == true) return@withLock

                syncJob = launch(dispatcherProvider.io) {
                    networkConnectionMonitor.networkConnectionStatus
                        .flatMapLatest { status ->
                            if (status is NetworkConnectionStatus.Connected) {
                                repository.visitedStopEventsFlow(Constants.STOP_EVENTS_UPLOAD_BATCH_SIZE)
                            } else {
                                emptyFlow()
                            }
                        }
                        .collect { events ->
                            if (events.isNotEmpty()) {
                                repository.sendVisitedStopEvents(events)
                                // TODO: Implement a cooldown in onFailure if needed
                            }
                        }
                }
            }
        }
    }

    suspend fun executeDirectly() = withContext(dispatcherProvider.io) {
        mutex.withLock {
            if (syncJob?.isActive == true) return@withContext

            syncJob = coroutineContext[Job]

            try {
                repository.visitedStopEventsFlow(Constants.STOP_EVENTS_UPLOAD_BATCH_SIZE)
                    .transformWhile { events ->
                        emit(events)
                        events.isNotEmpty()
                    }
                    .collect { events ->
                        if (events.isNotEmpty()) {
                            repository.sendVisitedStopEvents(events)
                                .onFailure {
                                    currentCoroutineContext().cancel(
                                        CancellationException("Network sync failed: ${it.message}")
                                    )
                                }
                        }
                    }
            } finally {
                mutex.withLock { syncJob = null }
            }
        }
    }
}