package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WorldCityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorldCityDao {
  @Query("SELECT * FROM world_cities ORDER BY sortOrder ASC, id ASC")
  fun observeAllCities(): Flow<List<WorldCityEntity>>

  @Query("SELECT COUNT(*) FROM world_cities")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(cities: List<WorldCityEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(city: WorldCityEntity)

  @Query("DELETE FROM world_cities WHERE id = :id")
  suspend fun deleteById(id: Long)
}
