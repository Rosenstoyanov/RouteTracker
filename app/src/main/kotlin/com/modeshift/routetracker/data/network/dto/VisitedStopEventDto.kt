package com.modeshift.routetracker.data.network.dto

import com.modeshift.models.EventType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@OptIn(ExperimentalTime::class)
data class VisitedStopEventDto(
    @SerialName("vehicleId")
    val appUser: String,
    @SerialName("stopId")
    val stopId: String,
    @SerialName("coordinates")
    val location: LocationDto,
    @SerialName("eventDateTime")
    val eventDateTime: Instant, // TODO: Add type converter sample: "2024-01-15T08:30:00Z"
    @SerialName("evenType")
    val evenType: EventType // TODO: Check weather a converter was needed
)