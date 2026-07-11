package com.example.sudoku.data.repository

import com.example.sudoku.data.local.dao.GameDao
import com.example.sudoku.data.local.entity.GameEntity
import com.example.sudoku.domain.model.*
import com.example.sudoku.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao
) : GameRepository {

    override fun getActiveGame(): Flow<GameSession?> {
        return gameDao.getActiveGame().map { it?.toDomain() }
    }

    override suspend fun saveGame(game: GameSession) {
        gameDao.saveGame(game.toEntity())
    }

    override fun getGameHistory(): Flow<List<GameSession>> {
        return gameDao.getAllGames().map { list -> list.map { it.toDomain() } }
    }

    private fun GameEntity.toDomain(): GameSession {
        val boardCells = List(81) { i ->
            Cell(
                value = boardValues[i].digitToInt(),
                isFixed = fixedMask[i] == '1',
                // Simplified notes mapping for now
                notes = emptySet() 
            )
        }
        val solutionCells = List(81) { i ->
            Cell(value = solutionValues[i].digitToInt(), isFixed = true)
        }
        
        return GameSession(
            id = gameId,
            board = Board(boardCells),
            solution = Board(solutionCells),
            difficulty = difficulty,
            status = status,
            elapsedSeconds = elapsedSeconds,
            mistakes = mistakes,
            hintsUsed = hintsUsed
        )
    }

    private fun GameSession.toEntity(): GameEntity {
        return GameEntity(
            gameId = id,
            difficulty = difficulty,
            status = status,
            boardValues = board.cells.joinToString("") { it.value.toString() },
            fixedMask = board.cells.joinToString("") { if (it.isFixed) "1" else "0" },
            solutionValues = solution.cells.joinToString("") { it.value.toString() },
            notesJson = "{}", // Simplified for now
            elapsedSeconds = elapsedSeconds,
            mistakes = mistakes,
            hintsUsed = hintsUsed,
            updatedAt = System.currentTimeMillis()
        )
    }
}
