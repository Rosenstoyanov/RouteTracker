package com.modeshift.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import com.modeshift.database.entity.RouteEntity
import com.modeshift.database.entity.StopEntity

@Database(
    entities = [RouteEntity::class, StopEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class RouteTrackerDatabase : RoomDatabase() {
    abstract fun RoutesDao(): RoutesDao
    abstract fun StopsDao(): StopsDao
}