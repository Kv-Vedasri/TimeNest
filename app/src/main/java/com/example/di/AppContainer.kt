package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.TimeNestDatabase
import com.example.data.local.prefs.UserPreferencesDataSource
import com.example.data.repository.TimeNestRepository

class AppContainer(context: Context) {
  private val database: TimeNestDatabase =
    Room.databaseBuilder(
        context.applicationContext,
        TimeNestDatabase::class.java,
        "timenest_db",
      )
      .fallbackToDestructiveMigration(dropAllTables = false)
      .build()

  private val prefsDataSource = UserPreferencesDataSource(context.applicationContext)

  val repository: TimeNestRepository =
    TimeNestRepository(
      prefsDataSource = prefsDataSource,
      activityRecordDao = database.activityRecordDao(),
      worldCityDao = database.worldCityDao(),
    )
}
