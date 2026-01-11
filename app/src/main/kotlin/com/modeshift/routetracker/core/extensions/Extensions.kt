package com.modeshift.routetracker.core.extensions

import com.google.android.gms.maps.model.LatLng
import com.modeshift.models.Location
import com.modeshift.routetracker.core.ui.models.StopPin
import com.modeshift.routetracker.domain.models.Stop

fun Location.toLatLng() = LatLng(latitude, longitude)

fun Stop.toStopPin() = StopPin(
    pinPosition = location.toLatLng(),
    pinTitle = name,
    stop = this
)