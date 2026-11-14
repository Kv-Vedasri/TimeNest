package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ActivityRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityRecordDao {
  @Query("SELECT * FROM activity_records WHERE createdAtMillis >= :startOfDayMillis ORDER BY createdAtMillis DESC")
  fun observeRecordsSince(startOfDayMillis: Long): Flow<List<ActivityRecordEntity>>

  @Query("SELECT MIN(scoreValue) FROM activity_records WHERE type = 'REACTION_GAME' AND scoreValue > 0")
  fun observeBestReactionMs(): Flow<Int?>

  @Query("SELECT * FROM activity_records WHERE type = 'REACTION_GAME' ORDER BY createdAtMillis DESC LIMIT 5")
  fun observeRecentReactionScores(): Flow<List<ActivityRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: ActivityRecordEntity)

  @Query("DELETE FROM activity_records")
  suspend fun clearAll()
}
