// File: repository/ScoreRepository.kt
package com.kotlin.tictactoe.repository

import com.kotlin.tictactoe.data.ScoreDao
import com.kotlin.tictactoe.data.ScoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScoreRepository(private val dao: ScoreDao) {
    fun getScores(): Flow<ScoreEntity> =
        dao.getScores().map { it ?: ScoreEntity(id = 0, humanWins = 0, aiWins = 0, draws = 0) }

    suspend fun incrementHumanWin() {
        val current = dao.getScores().let { /* can't call Flow directly; callers should read repo's flow */ }
        // We'll implement read-modify-write from ViewModel using get + upsert for simplicity.
    }

    suspend fun upsert(score: ScoreEntity) {
        dao.upsert(score)
    }
}
