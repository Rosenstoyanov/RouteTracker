package com.modeshift.routetracker.core.network.httpclient

import io.ktor.client.plugins.logging.Logger
import timber.log.Timber

class HttpLogger : Logger {
    override fun log(message: String) {
        Timber.i(message)
    }
}