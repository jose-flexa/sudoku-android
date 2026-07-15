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
    val selectedValue = selectedCell?.let { board[it.first, it.second].value }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(2.dp, Color.Black)
    ) {
        for (blockRow in 0 until 3) {
            Row(modifier = Modifier.weight(1f)) {
                for (blockCol in 0 until 3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, Color.Black)
                    ) {
                        Column {
                            for (rowInBlock in 0 until 3) {
                                Row(modifier = Modifier.weight(1f)) {
                                    for (colInBlock in 0 until 3) {
                                        val row = blockRow * 3 + rowInBlock
                                        val col = blockCol * 3 + colInBlock
                                        val isSelected = selectedCell?.first == row && selectedCell?.second == col
                                        val cell = board[row, col]
                                        val isSameDigit = selectedValue != 0 && cell.value == selectedValue

                                        SudokuCell(
                                            cell = cell,
                                            isSelected = isSelected,
                                            isSameDigit = isSameDigit,
                                            onClick = { onCellClick(row, col) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .border(0.5.dp, Color.LightGray)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuCell(
    cell: Cell,
    isSelected: Boolean,
    isSameDigit: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                when {
                    isSelected -> Color.Cyan.copy(alpha = 0.3f)
                    isSameDigit -> Color.Yellow.copy(alpha = 0.3f)
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
