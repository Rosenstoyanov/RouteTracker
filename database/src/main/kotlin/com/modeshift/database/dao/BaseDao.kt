package com.modeshift.database.dao

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface BaseDao<T> {
    @Upsert
    suspend fun upsert(entity: T)

    @Upsert
    suspend fun upsert(entities: List<T>)
}