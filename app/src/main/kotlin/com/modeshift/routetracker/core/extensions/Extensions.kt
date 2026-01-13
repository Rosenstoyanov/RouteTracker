package com.modeshift.routetracker.core.extensions

import com.modeshift.models.Location

fun android.location.Location.toModel() = Location(
    latitude = latitude,
    longitude = longitude
)

fun Location.toLocation() = android.location.Location("").apply {
    latitude = this@apply.latitude
    longitude = this@apply.longitude
}