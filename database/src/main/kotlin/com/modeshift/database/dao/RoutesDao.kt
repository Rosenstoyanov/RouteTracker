package com.modeshift.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.modeshift.database.entity.RouteEntity

@Dao
interface RoutesDao {

    @Query("SELECT * FROM Routes")
    suspend fun getAllRoutes(): List<RouteEntity>

    @Insert
    suspend fun insert(entities: List<RouteEntity>)

    @Query("DELETE FROM Routes")
    suspend fun deleteAllRoutes()

    @Transaction
    suspend fun replaceRoutes(routes: List<RouteEntity>) {
        deleteAllRoutes()
        insert(routes)
    }
}