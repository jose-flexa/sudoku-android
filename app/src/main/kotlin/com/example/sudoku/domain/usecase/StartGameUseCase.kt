package com.example.sudoku.domain.usecase

import com.example.sudoku.domain.model.*
import com.example.sudoku.domain.repository.GameRepository
import com.example.sudoku.puzzle.PuzzleGenerator
import java.util.UUID
import javax.inject.Inject

class StartGameUseCase @Inject constructor(
    private val puzzleGenerator: PuzzleGenerator,
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(difficulty: Difficulty): GameSession {
        val (boardValues, solutionValues) = puzzleGenerator.generate(difficulty)
        
        val boardCells = List(81) { i ->
            Cell(
                value = boardValues[i],
                isFixed = boardValues[i] != 0
            )
        }
        val solutionCells = List(81) { i ->
            Cell(value = solutionValues[i], isFixed = true)
        }

        val gameSession = GameSession(
            id = UUID.randomUUID().toString(),
            board = Board(boardCells),
            solution = Board(solutionCells),
            difficulty = difficulty,
            status = GameStatus.ACTIVE,
            elapsedSeconds = 0,
            mistakes = 0,
            remainingHints = 3
        )
        
        gameRepository.saveGame(gameSession)
        return gameSession
    }
}
