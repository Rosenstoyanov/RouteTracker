package com.modeshift.routetracker.data.network.dto

import com.modeshift.models.EventType
import com.modeshift.routetracker.core.network.serializers.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class VisitedStopEventDto(
    @SerialName("vehicleId")
    val appUser: String,
    @SerialName("stopId")
    val stopId: Long,
    @SerialName("coordinates")
    val location: LocationDto,
    @Serializable(with = InstantSerializer::class)
    @SerialName("eventDateTime")
    val eventDateTime: Instant,
    @SerialName("evenType")
    val evenType: EventType
)