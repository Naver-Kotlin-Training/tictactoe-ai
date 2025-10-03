// File: ai/MinimaxEngineTest.kt
package com.kotlin.tictactoe

import com.kotlin.tictactoe.model.Cell
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import com.kotlin.tictactoe.ai.MinimaxEngine

class MinimaxEngineTest {

    @Test
    fun `AI should block immediate human win`() = runTest {
        // Human X has two in a row, AI O must block
        val board = arrayOf(
            Cell.X, Cell.X, Cell.EMPTY,
            Cell.EMPTY, Cell.O, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        )
        val (move, _) = MinimaxEngine.bestMove(board)
        assertEquals(2, move) // block at index 2
    }

    @Test
    fun `AI should win if possible`() = runTest {
        // AI has two in a row and should complete
        val board = arrayOf(
            Cell.O, Cell.O, Cell.EMPTY,
            Cell.X, Cell.X, Cell.EMPTY,
            Cell.EMPTY, Cell.EMPTY, Cell.EMPTY
        )
        val (move, _) = MinimaxEngine.bestMove(board)
        assertEquals(2, move) // complete the winning row
    }

    @Test
    fun `AI should prefer center when board is empty`() = runTest {
        val board = Array(9) { Cell.EMPTY }
        val (move, trace) = MinimaxEngine.bestMove(board)
        // Usually, minimax picks center as optimal
        assertEquals(4, move)
        assertTrue(trace.isNotEmpty())
    }

    @Test
    fun `AI should result in draw against optimal human`() = runTest {
        // Simulate a board near end game
        val board = arrayOf(
            Cell.X, Cell.O, Cell.X,
            Cell.X, Cell.O, Cell.EMPTY,
            Cell.O, Cell.X, Cell.EMPTY
        )
        val (move, _) = MinimaxEngine.bestMove(board)
        assertTrue(move == 5 || move == 8) // both lead to a draw
    }
}
