package com.modeshift.database.di

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.modeshift.database.RemoteTrackerDatabase
import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLoggingDatabase(@ApplicationContext context: Context): RemoteTrackerDatabase {
        val builder = Room.databaseBuilder(
            context = context,
            klass = RemoteTrackerDatabase::class.java,
            name = "remote_tracker.db"
        )

//        if (Debug.isDebuggerConnected()) {
//            builder.allowMainThreadQueries()
//        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRoutesDao(db: RemoteTrackerDatabase): RoutesDao {
        return db.RoutesDao()
    }

    @Singleton
    @Provides
    fun provideStopsDao(db: RemoteTrackerDatabase): StopsDao {
        return db.StopsDao()
    }
}