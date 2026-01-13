package com.modeshift.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.modeshift.database.RouteTrackerDatabase
import com.modeshift.database.entity.VisitedStopEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitedStopEventsDao {
    @Query("SELECT * FROM VisitedStopEvents")
    suspend fun getAll(): List<VisitedStopEventEntity>

    @Insert
    suspend fun insert(visitedStopEvent: VisitedStopEventEntity)

    @Delete
    suspend fun delete(visitedStopEvents: List<VisitedStopEventEntity>)

    @Query("SELECT * FROM VisitedStopEvents ORDER BY event_date_time DESC LIMIT :limit")
    fun visitedStopEventsFlow(limit: Int): Flow<List<VisitedStopEventEntity>>

    @Query("UPDATE VisitedStopEvents SET failure_count = failure_count + 1 WHERE id IN (:ids)")
    suspend fun incrementFailureCount(ids: List<Long>)

    @Transaction
    suspend fun safeIncrementFailureCount(ids: List<Long>) {
        ids.chunked(RouteTrackerDatabase.MAX_ELEMENTS_IN_CLAUSE).forEach {
            incrementFailureCount(it)
        }
    }
}