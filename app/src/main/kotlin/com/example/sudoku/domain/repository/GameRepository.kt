package com.example.sudoku.domain.repository

import com.example.sudoku.domain.model.GameSession
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getActiveGame(): Flow<GameSession?>
    suspend fun saveGame(game: GameSession)
    fun getGameHistory(): Flow<List<GameSession>>
}
