package com.modeshift.database

import androidx.room.TypeConverter
import com.modeshift.models.EventType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class Converters {

    // --- Converter for EventType (Enum) ---
    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    @TypeConverter
    fun toEventType(value: String): EventType {
        return EventType.valueOf(value)
    }

    // --- Converter for DateTime (Instant) ---
    @TypeConverter
    fun fromInstant(value: Instant): String {
        return value.toString()
    }

    @TypeConverter
    fun toInstant(value: String): Instant {
        return Instant.parse(value)
    }
}