package com.modeshift.routetracker.core.network.status

sealed interface NetworkConnectionStatus {
    data object Disconnected : NetworkConnectionStatus
    data object Connected : NetworkConnectionStatus
}