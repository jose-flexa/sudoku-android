package com.example.sudoku.puzzle

class SudokuSolver {
    fun solve(board: IntArray): Boolean {
        for (i in 0 until 81) {
            if (board[i] == 0) {
                for (num in 1..9) {
                    if (isValid(board, i, num)) {
                        board[i] = num
                        if (solve(board)) return true
                        board[i] = 0
                    }
                }
                return false
            }
        }
        return true
    }

    fun isValid(board: IntArray, index: Int, num: Int): Boolean {
        val row = index / 9
        val col = index % 9

        // Check row
        for (i in 0 until 9) {
            if (board[row * 9 + i] == num) return false
        }

        // Check column
        for (i in 0 until 9) {
            if (board[i * 9 + col] == num) return false
        }

        // Check 3x3 box
        val startRow = (row / 3) * 3
        val startCol = (col / 3) * 3
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (board[(startRow + i) * 9 + (startCol + j)] == num) return false
            }
        }

        return true
    }
}
