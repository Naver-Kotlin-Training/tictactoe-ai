// File: model/GameModels.kt
package com.kotlin.tictactoe.model

enum class Cell { EMPTY, X, O } // X = human, O = AI

enum class Player { HUMAN, AI }

data class AiEvaluation(val index: Int, val score: Int) // for trace/visualization
