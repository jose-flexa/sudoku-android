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
                startNewGame(Difficulty.EASY)
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

        val newBoard = game.board.withCell(selected.first, selected.second) {
            it.copy(value = number, isError = number != 0 && number != game.solution[selected.first, selected.second].value)
        }
        
        val newMistakes = if (number != 0 && number != game.solution[selected.first, selected.second].value) {
            game.mistakes + 1
        } else {
            game.mistakes
        }

        val updatedGame = game.copy(board = newBoard, mistakes = newMistakes)
        
        // Check if won: all cells filled and no errors
        val isWon = updatedGame.board.cells.all { it.value != 0 && !it.isError }
        val finalGame = if (isWon) updatedGame.copy(status = GameStatus.WON) else updatedGame

        currentGame = finalGame
        updateStateWithGame(finalGame)
        
        viewModelScope.launch {
            gameRepository.saveGame(updatedGame)
        }
    }

    override fun showClue() {
        TODO("Not yet implemented")
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
                hintsUsed = game.hintsUsed,
                isLoading = false,
                completedDigits = completedDigits
            )
        }
    }
}
