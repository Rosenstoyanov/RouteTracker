package com.modeshift.database

import androidx.room.TypeConverter
import com.modeshift.models.EventType
import java.time.Instant
import kotlin.time.ExperimentalTime

class Converters {

    // Converter for EventType
    @TypeConverter
    fun fromEventType(value: EventType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEventType(value: String?): EventType? {
        return value?.let { EventType.valueOf(it) }
    }

    // Converter for Instant
    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun fromInstant(value: Instant?): String? {
        return value?.toString()
    }

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun toInstant(value: String?): Instant? {
        return value?.let { Instant.parse(value) }
    }
}