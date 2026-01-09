package com.modeshift.routetracker.core.network.status

import kotlinx.coroutines.flow.StateFlow

interface NetworkConnectionMonitor {
    val networkConnectionStatus: StateFlow<NetworkConnectionStatus>

    fun isConnected(): Boolean

    fun connect()
    fun disconnect()
}