package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.ActivityRecordDao
import com.example.data.local.dao.WorldCityDao
import com.example.data.local.entity.ActivityRecordEntity
import com.example.data.local.entity.WorldCityEntity

@Database(
  entities = [ActivityRecordEntity::class, WorldCityEntity::class],
  version = 1,
  exportSchema = false,
)
abstract class TimeNestDatabase : RoomDatabase() {
  abstract fun activityRecordDao(): ActivityRecordDao
  abstract fun worldCityDao(): WorldCityDao
}
