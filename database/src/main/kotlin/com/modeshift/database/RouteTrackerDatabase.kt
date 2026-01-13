package com.modeshift.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import com.modeshift.database.dao.VisitedStopEventsDao
import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity
import com.modeshift.database.entity.VisitedStopEventEntity

@Database(
    entities = [RouteEntity::class, StopEntity::class, VisitedStopEventEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class RouteTrackerDatabase : RoomDatabase() {
    abstract fun RoutesDao(): RoutesDao
    abstract fun StopsDao(): StopsDao
    abstract fun VisitedStopEventsDao(): VisitedStopEventsDao

    companion object {
        const val DATABASE_NAME = "remote_tracker.db"
        const val MAX_ELEMENTS_IN_CLAUSE = 999
    }
}