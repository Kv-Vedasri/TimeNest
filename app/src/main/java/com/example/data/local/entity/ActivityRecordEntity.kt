package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_records")
data class ActivityRecordEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val type: String, // "FOCUS", "TIMER", "REACTION_GAME"
  val durationSeconds: Int,
  val scoreValue: Int = 0, // e.g. reaction time in ms
  val label: String,
  val createdAtMillis: Long = System.currentTimeMillis(),
)
