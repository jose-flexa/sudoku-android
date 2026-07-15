package com.example.sudoku.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sudoku.domain.model.Difficulty
import com.example.sudoku.domain.model.GameStatus

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Mistakes: ${uiState.mistakes}", style = MaterialTheme.typography.titleMedium)
                Text(text = "Time: ${formatTime(uiState.elapsedSeconds)}", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            SudokuBoard(
                board = uiState.board,
                selectedCell = uiState.selectedCell,
                onCellClick = viewModel::onCellSelected,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (uiState.status == GameStatus.WON) {
                Text(text = "Congratulations! You won!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.startNewGame(Difficulty.EASY) }) {
                    Text(text = "New Game")
                }
            } else {
                NumberKeyboard(
                    onNumberClick = viewModel::onNumberInput,
                    completedDigits = uiState.completedDigits,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

@Composable
fun NumberKeyboard(
    onNumberClick: (Int) -> Unit,
    completedDigits: Set<Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            for (i in 1..5) {
                if (i !in completedDigits) {
                    Button(onClick = { onNumberClick(i) }) {
                        Text(text = i.toString())
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp)) // Maintain layout spacing
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            for (i in 6..9) {
                if (i !in completedDigits) {
                    Button(onClick = { onNumberClick(i) }) {
                        Text(text = i.toString())
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
            Button(onClick = { onNumberClick(0) }) {
                Text(text = "X")
            }
        }
    }
}
