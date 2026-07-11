package com.example.sudoku.data.local.dao

import androidx.room.*
import com.example.sudoku.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE status = 'ACTIVE' ORDER BY updatedAt DESC LIMIT 1")
    fun getActiveGame(): Flow<GameEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(game: GameEntity)

    @Query("SELECT * FROM games ORDER BY updatedAt DESC")
    fun getAllGames(): Flow<List<GameEntity>>
}
