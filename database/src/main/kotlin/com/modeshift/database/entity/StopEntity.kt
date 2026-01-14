package com.modeshift.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.modeshift.models.Location

@Entity(
    tableName = "Stops",
    indices = [
        Index(value = ["location_latitude", "location_longitude"])
    ]
)
data class StopEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "route_id")
    val routeId: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @Embedded(prefix = "location_")
    val location: Location,
    @ColumnInfo(name = "radius")
    val radius: Long
)