package com.modeshift.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.modeshift.database.entity.StopEntity

@Dao
interface StopsDao {
    @Query("SELECT * FROM Stops")
    suspend fun getAllStops(): List<StopEntity>

    @Insert
    suspend fun insert(entities: List<StopEntity>)

    @Query("DELETE FROM Stops")
    suspend fun deleteAllStops()

    @Transaction
    suspend fun replaceAllStops(stops: List<StopEntity>) {
        deleteAllStops()
        insert(stops)
    }

    @Query(
        """
    SELECT * FROM stops 
    WHERE location_latitude BETWEEN :minLat AND :maxLat
    AND location_longitude BETWEEN :minLng AND :maxLng
    """
    )
    suspend fun getStopsInArea(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<StopEntity>
}