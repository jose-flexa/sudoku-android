package com.example.sudoku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sudoku.domain.model.Difficulty
import com.example.sudoku.domain.model.GameStatus

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val gameId: String,
    val difficulty: Difficulty,
    val status: GameStatus,
    val boardValues: String, // 81 chars
    val fixedMask: String,   // 81 chars
    val solutionValues: String, // 81 chars
    val notesJson: String,
    val elapsedSeconds: Int,
    val mistakes: Int,
    val remainingHints: Int,
    val updatedAt: Long
)
