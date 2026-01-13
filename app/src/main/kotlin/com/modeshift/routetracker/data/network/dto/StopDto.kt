package com.modeshift.routetracker.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StopDto(
    @SerialName("id")
    val id: Long,
    @SerialName("routeId")
    val routeId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("radius")
    val radius: Long
)