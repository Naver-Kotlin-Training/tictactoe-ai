// File: ai/HeuristicAi.kt
package com.kotlin.tictactoe.ai

import com.kotlin.tictactoe.model.Cell
import com.kotlin.tictactoe.model.AiEvaluation

/**
 * AI opponent with 3 difficulty levels:
 * - EASY: Random valid moves
 * - MEDIUM: Heuristic (center > corners > edges)
 * - HARD: Full Minimax with Alpha-Beta pruning
 */
object HeuristicAi {

    enum class Difficulty { EASY, MEDIUM, HARD }

    /**
     * Returns a Pair of (chosenMoveIndex, trace).
     * Trace is only populated for HARD mode.
     */
    fun selectMove(board: Array<Cell>, difficulty: Difficulty): Pair<Int, List<AiEvaluation>> {
        return when (difficulty) {
            Difficulty.EASY -> randomMove(board) to emptyList()
            Difficulty.MEDIUM -> mediumMove(board) to emptyList()
            Difficulty.HARD -> MinimaxEngine.bestMoveBlocking(board)
        }
    }

    // ---------------------------
    // EASY
    // ---------------------------
    private fun randomMove(board: Array<Cell>): Int {
        val available = board.indices.filter { board[it] == Cell.EMPTY }
        return available.random()
    }

    // ---------------------------
    // MEDIUM
    // ---------------------------
    private fun mediumMove(board: Array<Cell>): Int {
        // Prioritize: center -> corners -> edges
        if (board[4] == Cell.EMPTY) return 4
        val corners = listOf(0, 2, 6, 8).filter { board[it] == Cell.EMPTY }
        if (corners.isNotEmpty()) return corners.random()
        val edges = listOf(1, 3, 5, 7).filter { board[it] == Cell.EMPTY }
        return edges.random()
    }

    // ---------------------------
    // HARD (delegate to MinimaxEngine)
    // ---------------------------
    private object MinimaxEngine {

        private val winPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )

        private fun checkWinner(board: Array<Cell>): Cell? {
            for (p in winPatterns) {
                val a = board[p[0]]
                if (a != Cell.EMPTY && a == board[p[1]] && a == board[p[2]]) return a
            }
            return null
        }

        private fun isFull(board: Array<Cell>) = board.none { it == Cell.EMPTY }

        private fun minimax(
            board: Array<Cell>,
            depth: Int,
            isMax: Boolean,
            alpha: Int,
            beta: Int,
            trace: MutableList<AiEvaluation>
        ): Int {
            val winner = checkWinner(board)
            if (winner == Cell.O) return 10 - depth
            if (winner == Cell.X) return depth - 10
            if (isFull(board)) return 0

            var a = alpha
            var b = beta
            val available = board.indices.filter { board[it] == Cell.EMPTY }

            return if (isMax) {
                var best = Int.MIN_VALUE
                for (i in available) {
                    board[i] = Cell.O
                    val score = minimax(board, depth + 1, false, a, b, trace)
                    board[i] = Cell.EMPTY
                    if (score > best) best = score
                    if (depth == 0) trace.add(AiEvaluation(i, score))
                    a = maxOf(a, score)
                    if (b <= a) break
                }
                best
            } else {
                var best = Int.MAX_VALUE
                for (i in available) {
                    board[i] = Cell.X
                    val score = minimax(board, depth + 1, true, a, b, trace)
                    board[i] = Cell.EMPTY
                    if (score < best) best = score
                    b = minOf(b, score)
                    if (b <= a) break
                }
                best
            }
        }

        fun bestMoveBlocking(boardIn: Array<Cell>): Pair<Int, List<AiEvaluation>> {
            val board = boardIn.copyOf()
            val trace = mutableListOf<AiEvaluation>()

            val bestScore = minimax(board, 0, true, Int.MIN_VALUE, Int.MAX_VALUE, trace)
            val bestMoves = trace.filter { it.score == bestScore }
            val chosen = bestMoves.firstOrNull()?.index
                ?: board.indices.first { board[it] == Cell.EMPTY }

            return chosen to trace
        }
    }
}
