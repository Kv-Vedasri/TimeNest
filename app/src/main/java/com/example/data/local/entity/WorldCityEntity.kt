package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_cities")
data class WorldCityEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val zoneId: String,
  val cityName: String,
  val regionLabel: String,
  val sortOrder: Int = 0,
)
