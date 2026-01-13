package com.modeshift.database.di

import android.content.Context
import androidx.room.Room
import com.modeshift.database.RouteTrackerDatabase
import com.modeshift.database.dao.RoutesDao
import com.modeshift.database.dao.StopsDao
import com.modeshift.database.dao.VisitedStopEventsDao
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
    fun provideLoggingDatabase(@ApplicationContext context: Context): RouteTrackerDatabase {
        val builder = Room.databaseBuilder(
            context = context,
            klass = RouteTrackerDatabase::class.java,
            name = "remote_tracker.db"
        ).fallbackToDestructiveMigration(true)

//        if (Debug.isDebuggerConnected()) {
//            builder.allowMainThreadQueries()
//        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRoutesDao(db: RouteTrackerDatabase): RoutesDao {
        return db.RoutesDao()
    }

    @Singleton
    @Provides
    fun provideStopsDao(db: RouteTrackerDatabase): StopsDao {
        return db.StopsDao()
    }

    @Singleton
    @Provides
    fun provideVisitedStopEventsDao(db: RouteTrackerDatabase): VisitedStopEventsDao {
        return db.VisitedStopEventsDao()
    }
}