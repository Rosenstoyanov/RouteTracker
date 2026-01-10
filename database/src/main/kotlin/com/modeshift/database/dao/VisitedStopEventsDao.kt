package com.modeshift.database.dao

import androidx.room.Dao
import androidx.room.Insert
import com.modeshift.database.entity.VisitedStopEventEntity

@Dao
interface VisitedStopEventsDao {
    @Insert
    suspend fun insertVisitedStopEvent(visitedStopEvent: VisitedStopEventEntity)
}