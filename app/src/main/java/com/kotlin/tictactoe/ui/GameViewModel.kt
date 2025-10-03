// File: ui/GameViewModel.kt
package com.kotlin.tictactoe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.tictactoe.ai.HeuristicAi
import com.kotlin.tictactoe.data.ScoreEntity
import com.kotlin.tictactoe.model.AiEvaluation
import com.kotlin.tictactoe.model.Cell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AiDifficulty { EASY, MEDIUM, HARD }

data class GameUiState(
    val board: List<Cell> = List(9) { Cell.EMPTY },
    val currentTurn: Cell = Cell.X, // human starts by default
    val isGameOver: Boolean = false,
    val winner: Cell? = null,
    val aiTrace: List<AiEvaluation> = emptyList(),
    val aiThinking: Boolean = false,
    val humanScore: Int = 0,
    val aiScore: Int = 0,
    val draws: Int = 0,
    val difficulty: AiDifficulty = AiDifficulty.MEDIUM
)

class GameViewModel(
    private val repositoryFlow: Flow<ScoreEntity>,
    private val scoreUpsert: suspend (ScoreEntity) -> Unit
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repositoryFlow.collect { entity ->
                _state.update { s ->
                    s.copy(
                        humanScore = entity.humanWins,
                        aiScore = entity.aiWins,
                        draws = entity.draws
                    )
                }
            }
        }
    }

    fun setDifficulty(d: AiDifficulty) {
        _state.update { it.copy(difficulty = d) }
    }

    fun resetGame(humanStarts: Boolean = true) {
        _state.update {
            val start = if (humanStarts) Cell.X else Cell.O
            it.copy(
                board = List(9) { Cell.EMPTY },
                currentTurn = start,
                isGameOver = false,
                winner = null,
                aiTrace = emptyList()
            )
        }
        if (!humanStarts) performAiMove()
    }

    private fun checkWinner(board: List<Cell>): Cell? {
        val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (p in winPatterns) {
            val a = board[p[0]]
            if (a != Cell.EMPTY && a == board[p[1]] && a == board[p[2]]) return a
        }
        return null
    }

    private fun isFull(board: List<Cell>) = board.none { it == Cell.EMPTY }

    fun humanMove(index: Int) {
        val cur = _state.value
        if (cur.isGameOver) return
        if (cur.currentTurn != Cell.X) return
        if (cur.board[index] != Cell.EMPTY) return

        val newBoard = cur.board.toMutableList()
        newBoard[index] = Cell.X

        updateAfterHumanMove(newBoard)
    }

    private fun updateAfterHumanMove(newBoard: List<Cell>) {
        val winner = checkWinner(newBoard)
        val full = isFull(newBoard)

        _state.update { s ->
            s.copy(board = newBoard, currentTurn = Cell.O)
        }

        if (winner != null || full) {
            finishGame(winner)
            return
        }

        performAiMove()
    }

    private fun finishGame(winner: Cell?) {
        _state.update { s -> s.copy(isGameOver = true, winner = winner) }
        viewModelScope.launch(Dispatchers.IO) {
            val current = _state.value
            val newScores = ScoreEntity(
                id = 0,
                humanWins = if (winner == Cell.X) current.humanScore + 1 else current.humanScore,
                aiWins = if (winner == Cell.O) current.aiScore + 1 else current.aiScore,
                draws = if (winner == null && isFull(current.board)) current.draws + 1 else current.draws
            )
            scoreUpsert(newScores)
        }
    }

    private fun performAiMove() {
        val cur = _state.value
        if (cur.isGameOver) return

        _state.update { it.copy(aiThinking = true, aiTrace = emptyList()) }

        viewModelScope.launch {
            val boardCopy = _state.value.board.toList()

            val (move, trace) = withContext(Dispatchers.Default) {
                val diff = when (cur.difficulty) {
                    AiDifficulty.EASY -> HeuristicAi.Difficulty.EASY
                    AiDifficulty.MEDIUM -> HeuristicAi.Difficulty.MEDIUM
                    AiDifficulty.HARD -> HeuristicAi.Difficulty.HARD
                }
                HeuristicAi.selectMove(boardCopy.toTypedArray(), diff)
            }

            applyAiMove(move, trace)
        }
    }

    private fun applyAiMove(moveIndex: Int, trace: List<AiEvaluation>) {
        val cur = _state.value
        if (cur.isGameOver) return
        if (cur.board[moveIndex] != Cell.EMPTY) {
            val fallback = cur.board.indices.firstOrNull { cur.board[it] == Cell.EMPTY } ?: return
            applyAiMove(fallback, listOf())
            return
        }

        val newBoard = cur.board.toMutableList()
        newBoard[moveIndex] = Cell.O

        _state.update { s ->
            s.copy(
                aiThinking = false,
                aiTrace = trace,
                board = newBoard,
                currentTurn = Cell.X
            )
        }

        val winner = checkWinner(newBoard)
        val full = isFull(newBoard)
        if (winner != null || full) finishGame(winner)
    }
}
