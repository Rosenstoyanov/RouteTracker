package com.modeshift.routetracker.core.network.status

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.modeshift.routetracker.core.network.status.NetworkConnectionStatus.Connected
import com.modeshift.routetracker.core.network.status.NetworkConnectionStatus.Disconnected
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionMonitorImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val networkRequest: NetworkRequest,
) : NetworkConnectionMonitor {

    private val _networkStatus = MutableStateFlow<NetworkConnectionStatus>(
        Disconnected,
    )
    override val networkConnectionStatus: StateFlow<NetworkConnectionStatus> = _networkStatus
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            val connected = connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: true
            _networkStatus.update {
                if (connected) {
                    Connected
                } else {
                    Disconnected
                }
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _networkStatus.update { Disconnected }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            _networkStatus.update { Disconnected }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            capabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, capabilities)
            val connected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            _networkStatus.update {
                if (connected) {
                    Connected
                } else {
                    Disconnected
                }
            }
        }
    }

    init {
        connect()
    }

    override fun isConnected(): Boolean {
        return _networkStatus.value is Connected
    }

    override fun connect() {
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun disconnect() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}