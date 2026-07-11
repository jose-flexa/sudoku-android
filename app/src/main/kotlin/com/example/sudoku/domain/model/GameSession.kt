package com.example.sudoku.domain.model

data class GameSession(
    val id: String,
    val board: Board,
    val solution: Board,
    val difficulty: Difficulty,
    val status: GameStatus,
    val elapsedSeconds: Int,
    val mistakes: Int,
    val hintsUsed: Int
)
