package com.modeshift.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.modeshift.database.entity.VisitedStopEventEntity

@Dao
interface VisitedStopEventsDao {
    @Insert
    suspend fun insert(visitedStopEvent: VisitedStopEventEntity)

    @Delete
    suspend fun delete(visitedStopEvents: List<VisitedStopEventEntity>)
}