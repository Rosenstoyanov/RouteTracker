package com.modeshift.routetracker.domain.models

import com.modeshift.models.EventType
import com.modeshift.models.Location
import java.time.Instant

data class VisitedStopEvent(
    val id: Long = 0,
    val appUser: String,
    val stopId: Long,
    val location: Location,
    val eventDateTime: Instant,
    val evenType: EventType
)
