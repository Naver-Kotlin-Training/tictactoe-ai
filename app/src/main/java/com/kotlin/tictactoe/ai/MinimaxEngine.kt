// File: ai/MinimaxEngine.kt
package com.kotlin.tictactoe.ai

import com.kotlin.tictactoe.model.AiEvaluation
import com.kotlin.tictactoe.model.Cell
import kotlinx.coroutines.delay

/**
 * Board represented as Array<Cell> of size 9.
 * AI = O, HUMAN = X.
 */
object MinimaxEngine {

    private val winPatterns = listOf(
        listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
        listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
        listOf(0,4,8), listOf(2,4,6)
    )

    private fun checkWinner(board: Array<Cell>): Cell? {
        for (p in winPatterns) {
            val a = board[p[0]]
            if (a != Cell.EMPTY && a == board[p[1]] && a == board[p[2]]) return a
        }
        return null
    }

    private fun isFull(board: Array<Cell>) = board.none { it == Cell.EMPTY }

    /**
     * Returns Pair(bestIndex, bestScore)
     * score convention: +10 for AI win, -10 for human win, 0 for draw
     */
    private fun minimax(board: Array<Cell>, depth: Int, isMaximizing: Boolean, trace: MutableList<AiEvaluation>): Int {
        val winner = checkWinner(board)
        if (winner == Cell.O) return 10 - depth
        if (winner == Cell.X) return depth - 10
        if (isFull(board)) return 0

        val available = board.indices.filter { board[it] == Cell.EMPTY }
        if (isMaximizing) {
            var best = Int.MIN_VALUE
            for (i in available) {
                board[i] = Cell.O
                val score = minimax(board, depth + 1, false, trace)
                board[i] = Cell.EMPTY
                if (score > best) best = score
                // record evaluations for visualization (score at top-level depth only)
                if (depth == 0) trace.add(AiEvaluation(i, score))
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (i in available) {
                board[i] = Cell.X
                val score = minimax(board, depth + 1, true, trace)
                board[i] = Cell.EMPTY
                if (score < best) best = score
            }
            return best
        }
    }

    /**
     * Public API: compute best move. This is a suspendable function you can call on Dispatchers.Default
     * trace is populated with AiEvaluation entries for top-level moves.
     */
    suspend fun bestMove(boardIn: Array<Cell>): Pair<Int, List<AiEvaluation>> {
        // operate on a copy
        val board = boardIn.copyOf()
        val trace = mutableListOf<AiEvaluation>()
        val available = board.indices.filter { board[it] == Cell.EMPTY }

        // trivial case: only one move
        if (available.size == 1) {
            return available.first() to trace
        }

        // run minimax at top-level: collect move evals in trace
        val bestScore = minimax(board, 0, true, trace)
        // from trace, choose best index with max score (could be multiple)
        val bestMoves = trace.filter { it.score == bestScore }
        val chosen = bestMoves.firstOrNull()?.index ?: available.random()

        // optional small delay for UX (remove in unit tests)
        // delay(100) // comment/uncomment as you prefer

        return chosen to trace
    }
}
