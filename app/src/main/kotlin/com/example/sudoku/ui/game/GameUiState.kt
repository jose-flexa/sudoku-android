package com.example.sudoku.ui.game

import com.example.sudoku.domain.model.Board
import com.example.sudoku.domain.model.Difficulty
import com.example.sudoku.domain.model.GameStatus

data class GameUiState(
    val board: Board = Board(),
    val difficulty: Difficulty = Difficulty.EASY,
    val status: GameStatus = GameStatus.ACTIVE,
    val selectedCell: Pair<Int, Int>? = null,
    val elapsedSeconds: Int = 0,
    val mistakes: Int = 0,
    val hintsUsed: Int = 0,
    val isLoading: Boolean = false,
    val completedDigits: Set<Int> = emptySet()
)
