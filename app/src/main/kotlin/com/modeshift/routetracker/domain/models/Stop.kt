package com.modeshift.routetracker.domain.models

import com.modeshift.models.Location

data class Stop(
    val id: Long,
    val routeId: Long,
    val name: String,
    val location: Location,
    val radius: Long
)
