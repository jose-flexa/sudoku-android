package com.example.sudoku.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudoku.domain.model.Difficulty
import com.example.sudoku.domain.model.GameSession
import com.example.sudoku.domain.model.GameStatus
import com.example.sudoku.domain.repository.GameRepository
import com.example.sudoku.domain.usecase.StartGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val startGameUseCase: StartGameUseCase,
    private val gameRepository: GameRepository
) : ViewModel(), GameActions {

    private val _uiState = MutableStateFlow(GameUiState(isLoading = true))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var currentGame: GameSession? = null
    private var timerJob: Job? = null

    init {
        loadActiveGame()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                if (currentGame?.status == GameStatus.WON) break
                delay(1000)
                currentGame = currentGame?.let { 
                    val updated = it.copy(elapsedSeconds = it.elapsedSeconds + 1)
                    _uiState.update { state -> state.copy(elapsedSeconds = updated.elapsedSeconds) }
                    updated
                }
            }
        }
    }

    private fun loadActiveGame() {
        viewModelScope.launch {
            gameRepository.getActiveGame().firstOrNull()?.let { game ->
                currentGame = game
                updateStateWithGame(game)
                startTimer()
            } ?: run {
                startNewGame(Difficulty.MEDIUM)
            }
        }
    }

    override fun startNewGame(difficulty: Difficulty) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val game = startGameUseCase(difficulty)
            currentGame = game
            updateStateWithGame(game)
            startTimer()
        }
    }

    fun onCellSelected(row: Int, col: Int) {
        _uiState.update { it.copy(selectedCell = row to col) }
    }

    override fun onNumberInput(number: Int) {
        val selected = _uiState.value.selectedCell ?: return
        val game = currentGame ?: return
        
        val cell = game.board[selected.first, selected.second]
        if (cell.isFixed) return

        val isError = number != 0 && number != game.solution[selected.first, selected.second].value
        val newBoard = game.board.withCell(selected.first, selected.second) {
            it.copy(value = number, isError = isError)
        }
        
        val newMistakes = if (isError) {
            game.mistakes + 1
        } else {
            game.mistakes
        }

        val updatedGame = game.copy(board = newBoard, mistakes = newMistakes)
        
        // Check if won: all cells filled and no errors
        val isWon = updatedGame.board.cells.all { it.value != 0 && !it.isError }
        val finalGame = if (isWon) updatedGame.copy(status = GameStatus.WON) else updatedGame

        currentGame = finalGame
        
        // Update last error cell
        if (isError) {
            _uiState.update { it.copy(lastErrorCell = selected) }
        } else if (number == 0 || !isError) {
            // If erasing or correct value, and it was the last error cell, clear it
            if (_uiState.value.lastErrorCell == selected) {
                _uiState.update { it.copy(lastErrorCell = null) }
            }
        }

        updateStateWithGame(finalGame)
        
        viewModelScope.launch {
            gameRepository.saveGame(updatedGame)
        }
    }

    override fun onEraseLastError() {
        val errorCell = _uiState.value.lastErrorCell ?: return
        val game = currentGame ?: return

        val newBoard = game.board.withCell(errorCell.first, errorCell.second) {
            it.copy(value = 0, isError = false)
        }

        val updatedGame = game.copy(board = newBoard)
        currentGame = updatedGame
        
        _uiState.update { it.copy(lastErrorCell = null) }
        updateStateWithGame(updatedGame)

        viewModelScope.launch {
            gameRepository.saveGame(updatedGame)
        }
    }

    override fun showClue() {
        if (_uiState.value.remainingHints <= 0) return
        val game = currentGame ?: return
        val selectedCell = _uiState.value.selectedCell ?: return

        val (row, col) = selectedCell
        val cell = game.board[row, col]

        // Only show clue for empty, non-fixed cells
        if (!cell.isEmpty || cell.isFixed) return

        val correctValue = game.solution[row, col].value

        // Update the board with the correct value
        val newBoard = game.board.withCell(row, col) {
            it.copy(value = correctValue, isError = false, notes = emptySet())
        }

        val updatedGame = game.copy(
            board = newBoard,
            remainingHints = game.remainingHints - 1
        )

        // Clear last error if this hint filled it
        if (_uiState.value.lastErrorCell == selectedCell) {
            _uiState.update { it.copy(lastErrorCell = null) }
        }

        // Check if won: all cells filled and no errors
        val isWon = updatedGame.board.cells.all { it.value != 0 && !it.isError }
        val finalGame = if (isWon) updatedGame.copy(status = GameStatus.WON) else updatedGame

        currentGame = finalGame
        updateStateWithGame(finalGame)

        viewModelScope.launch {
            gameRepository.saveGame(finalGame)
        }
    }

    private fun updateStateWithGame(game: GameSession) {
        val completedDigits = (1..9).filter { digit ->
            val count = game.board.cells.count { it.value == digit && !it.isError }
            count == 9
        }.toSet()

        _uiState.update {
            it.copy(
                board = game.board,
                difficulty = game.difficulty,
                status = game.status,
                elapsedSeconds = game.elapsedSeconds,
                mistakes = game.mistakes,
                remainingHints = game.remainingHints,
                isLoading = false,
                completedDigits = completedDigits
            )
        }
    }
}
