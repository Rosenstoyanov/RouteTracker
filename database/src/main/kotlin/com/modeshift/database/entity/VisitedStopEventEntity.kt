package com.modeshift.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.modeshift.models.EventType
import com.modeshift.models.Location
import java.time.Instant

@Entity(tableName = "VisitedStopEvents")
data class VisitedStopEventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo("app_user")
    val appUser: String,
    @ColumnInfo("stop_id")
    val stopId: Long,
    @Embedded(prefix = "location_")
    val location: Location,
    @ColumnInfo("event_date_time")
    val eventDateTime: Instant,
    @ColumnInfo("even_type")
    val evenType: EventType,
    @ColumnInfo("failure_count")
    val failureCount: Long = 0
)