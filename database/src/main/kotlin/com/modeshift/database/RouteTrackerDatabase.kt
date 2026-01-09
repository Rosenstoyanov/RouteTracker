package com.modeshift.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import com.modeshift.database.entity.RoutesEntity
import com.modeshift.database.entity.StopsEntity

@Database(
    entities = [RoutesEntity::class, StopsEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class RemoteTrackerDatabase : RoomDatabase() {
    abstract fun RoutesDao(): RoutesDao
    abstract fun StopsDao(): StopsDao
}