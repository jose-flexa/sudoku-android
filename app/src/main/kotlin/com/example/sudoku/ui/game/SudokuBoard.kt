package com.example.sudoku.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoku.domain.model.Board
import com.example.sudoku.domain.model.Cell

@Composable
fun SudokuBoard(
    board: Board,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(2.dp, Color.Black)
    ) {
        for (row in 0 until 9) {
            Row(modifier = Modifier.weight(1f)) {
                for (col in 0 until 9) {
                    val isSelected = selectedCell?.first == row && selectedCell?.second == col
                    val cell = board[row, col]
                    SudokuCell(
                        cell = cell,
                        isSelected = isSelected,
                        onClick = { onCellClick(row, col) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.5.dp, Color.Gray)
                            // Thick borders for 3x3 grids
                            .padding(
                                start = if (col % 3 == 0) 1.dp else 0.dp,
                                end = if (col == 8) 1.dp else 0.dp,
                                top = if (row % 3 == 0) 1.dp else 0.dp,
                                bottom = if (row == 8) 1.dp else 0.dp
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuCell(
    cell: Cell,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                when {
                    isSelected -> Color.Cyan.copy(alpha = 0.3f)
                    cell.isFixed -> Color.LightGray.copy(alpha = 0.2f)
                    else -> Color.White
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != 0) {
            Text(
                text = cell.value.toString(),
                color = when {
                    cell.isError -> Color.Red
                    cell.isFixed -> Color.Black
                    else -> MaterialTheme.colorScheme.primary
                },
                fontSize = 20.sp
            )
        }
    }
}
