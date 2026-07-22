package com.example.sudoku.ui.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sudoku.domain.model.Difficulty
import com.example.sudoku.domain.model.GameStatus
import com.example.sudoku.R

interface GameActions {
    fun startNewGame(difficulty: Difficulty)
    fun onNumberInput(number: Int)
    fun showClue()
}
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
                Text(text = "${stringResource(R.string.mistakes)}: ${uiState.mistakes}", style = MaterialTheme.typography.titleMedium)
                Text(text = "${stringResource(R.string.time)}: ${formatTime(uiState.elapsedSeconds)}", style = MaterialTheme.typography.titleMedium)
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
                Text(text = stringResource(R.string.congratulations), style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.
                height(16.dp))
                Button(onClick = { viewModel.startNewGame(Difficulty.EASY) }) {
                    Text(text = stringResource(R.string.new_game))
                }
            } else {
                NumberKeyboard(
                    gameActions = viewModel,
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
    gameActions: GameActions,
    completedDigits: Set<Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            for (i in 1..9) {
                    OutlinedButton(onClick = { gameActions.onNumberInput(i) }, shape = RectangleShape, modifier = Modifier.weight(
                        1f
                    ).alpha(if (i in completedDigits) 0f else 1f)) {
                        Text(text = i.toString())
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { gameActions.startNewGame(Difficulty.EASY) }, shape = RectangleShape, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.reset_game))
            }
            Button(onClick = { gameActions.showClue() }, shape = RectangleShape, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.show_clue))
            }
        }
    }
}
