// File: ui/GameScreen.kt
package com.kotlin.tictactoe.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kotlin.tictactoe.model.AiEvaluation
import com.kotlin.tictactoe.model.Cell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TicTacToe Ai") },
                actions = {
                    TextButton(onClick = { viewModel.resetGame(humanStarts = true) }) {
                        Text("Reset")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { viewModel.resetGame(humanStarts = false) }) {
                        Text("AI Start")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            // Score row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("You: ${state.humanScore}")
                Text("Draws: ${state.draws}")
                Text("AI: ${state.aiScore}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Board
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                BoardComponent(
                    board = state.board,
                    onCellClick = { idx -> viewModel.humanMove(idx) },
                    cellSize = 96.dp
                )

                if (state.aiThinking) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Difficulty controls
            Text("Difficulty:")
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedDifficulty(current = state.difficulty) {
                    viewModel.setDifficulty(it)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI trace visualization
            AiTraceView(trace = state.aiTrace)
            Spacer(modifier = Modifier.height(8.dp))

            // Game result
            state.winner?.let { winner ->
                val resultText = when (winner) {
                    Cell.X -> "You win!"
                    Cell.O -> "AI wins!"
                    else -> "Draw!"
                }
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun SegmentedDifficulty(current: AiDifficulty, onSelected: (AiDifficulty) -> Unit) {
    Row {
        listOf(AiDifficulty.EASY, AiDifficulty.MEDIUM, AiDifficulty.HARD).forEach { d ->
            val selected = d == current
            Button(
                onClick = { onSelected(d) },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(d.name)
            }
        }
    }
}

@Composable
fun AiTraceView(trace: List<AiEvaluation>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "AI evaluations:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (trace.isEmpty()) {
            Text("No trace available", style = MaterialTheme.typography.bodySmall)
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                trace.forEach { t ->
                    Card(
                        modifier = Modifier
                            .width(90.dp)
                            .height(60.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "Idx: ${t.index}",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                "Score: ${t.score}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BoardComponent(board: List<Cell>, onCellClick: (Int) -> Unit, cellSize: Dp) {
    val cols = 3
    Column {
        for (r in 0 until 3) {
            Row {
                for (c in 0 until 3) {
                    val idx = r * cols + c
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onCellClick(idx) },
                        contentAlignment = Alignment.Center
                    ) {
                        when (board.getOrNull(idx)) {
                            Cell.X -> DrawX()
                            Cell.O -> DrawO()
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawX() {
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val stroke = Stroke(width = size.minDimension * 0.08f, cap = StrokeCap.Round)
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
        drawLine(
            color = color,
            start = Offset(size.width, 0f),
            end = Offset(0f, size.height),
            strokeWidth = stroke.width,
            cap = stroke.cap
        )
    }
}

@Composable
fun DrawO() {
    val color = MaterialTheme.colorScheme.secondary
    Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val stroke = Stroke(width = size.minDimension * 0.08f)
        drawCircle(
            color = color,
            radius = size.minDimension / 2.5f,
            style = stroke
        )
    }
}
