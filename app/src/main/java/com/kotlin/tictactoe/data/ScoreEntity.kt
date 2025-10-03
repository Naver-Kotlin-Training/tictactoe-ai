// File: data/ScoreEntity.kt
package com.kotlin.tictactoe.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score_table")
data class ScoreEntity(
    @PrimaryKey val id: Int = 0, // singleton row
    val humanWins: Int = 0,
    val aiWins: Int = 0,
    val draws: Int = 0
)
