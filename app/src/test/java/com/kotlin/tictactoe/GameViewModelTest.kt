// File: ui/GameViewModelTest.kt
package com.kotlin.tictactoe

import com.kotlin.tictactoe.data.ScoreEntity
import com.kotlin.tictactoe.model.Cell
import com.kotlin.tictactoe.ui.GameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private lateinit var viewModel: GameViewModel
    private lateinit var scoreFlow: MutableSharedFlow<ScoreEntity>
    private val savedScores = mutableListOf<ScoreEntity>()

    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @Before
    fun setup() {
        scoreFlow = MutableSharedFlow(replay = 1)
        viewModel = GameViewModel(
            repositoryFlow = scoreFlow,
            scoreUpsert = { savedScores.add(it) }
        )
    }

    @Test
    fun `initial state has empty board and human turn`() = runTest {
        val state = viewModel.state.value
        assertEquals(9, state.board.size)
        assertTrue(state.board.all { it == Cell.EMPTY })
        assertEquals(Cell.X, state.currentTurn)
        assertFalse(state.isGameOver)
    }

    @Test
    fun `setDifficulty updates state`() = runTest {
        viewModel.setDifficulty(AiDifficulty.HARD)
        assertEquals(AiDifficulty.HARD, viewModel.state.value.difficulty)
    }

    @Test
    fun `humanMove updates board and triggers AI when valid`() = runTest(testDispatcher) {
        // push initial scores into flow
        scoreFlow.emit(ScoreEntity(0, 0, 0, 0))

        viewModel.humanMove(0)
        val afterMove = viewModel.state.value

        assertEquals(Cell.X, afterMove.board[0])
        // after human moves, AI turn should be next
        assertEquals(Cell.O, afterMove.currentTurn)
    }

    @Test
    fun `resetGame clears board and sets starter`() = runTest {
        viewModel.humanMove(0)
        viewModel.resetGame()
        val reset = viewModel.state.value

        assertTrue(reset.board.all { it == Cell.EMPTY })
        assertEquals(Cell.X, reset.currentTurn)
        assertFalse(reset.isGameOver)
    }

    @Test
    fun `finishGame updates scores`() = runTest(testDispatcher) {
        scoreFlow.emit(ScoreEntity(0, 0, 0, 0))
        // simulate finishing a game with human win
        val method = GameViewModel::class.java.getDeclaredMethod("finishGame", Cell::class.java)
        method.isAccessible = true
        method.invoke(viewModel, Cell.X)

        advanceUntilIdle()
        assertTrue(savedScores.isNotEmpty())
        assertEquals(1, savedScores.last().humanWins)
    }
}
