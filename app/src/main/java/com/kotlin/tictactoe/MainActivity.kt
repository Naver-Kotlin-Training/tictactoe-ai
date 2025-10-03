// File: MainActivity.kt
package com.kotlin.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.kotlin.tictactoe.data.AppDatabase
import com.kotlin.tictactoe.repository.ScoreRepository
import com.kotlin.tictactoe.ui.GameScreen
import com.kotlin.tictactoe.ui.GameViewModel
import kotlinx.coroutines.flow.first
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Build Room DB (production: use DI)
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tictactoe-db").build()
        val repo = ScoreRepository(db.scoreDao())

        // Create a simple factory for ViewModel so we can inject flows
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(repositoryFlow = repo.getScores(), scoreUpsert = { repo.upsert(it) }) as T
            }
        }

        setContent {
            // Provide your MaterialTheme (Material3)
            androidx.compose.material3.MaterialTheme {
                val vm: GameViewModel = viewModel(factory = factory)
                GameScreen(vm)
            }
        }
    }
}
