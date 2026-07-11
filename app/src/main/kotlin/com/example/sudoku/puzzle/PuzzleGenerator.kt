package com.example.sudoku.puzzle

import com.example.sudoku.domain.model.Difficulty
import javax.inject.Inject
import kotlin.random.Random

class PuzzleGenerator @Inject constructor() {
    private val solver = SudokuSolver()

    fun generate(difficulty: Difficulty): Pair<IntArray, IntArray> {
        val board = IntArray(81)
        fillDiagonal(board)
        solver.solve(board)
        val solution = board.copyOf()
        removeElements(board, difficulty)
        return Pair(board, solution)
    }

    private fun fillDiagonal(board: IntArray) {
        for (i in 0 until 9 step 3) {
            fillBox(board, i, i)
        }
    }

    private fun fillBox(board: IntArray, row: Int, col: Int) {
        val nums = (1..9).shuffled().iterator()
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                board[(row + i) * 9 + (col + j)] = nums.next()
            }
        }
    }

    private fun removeElements(board: IntArray, difficulty: Difficulty) {
        val count = when (difficulty) {
            Difficulty.EASY -> 30
            Difficulty.MEDIUM -> 40
            Difficulty.HARD -> 50
            Difficulty.EXPERT -> 60
        }
        var removed = 0
        while (removed < count) {
            val i = Random.nextInt(81)
            if (board[i] != 0) {
                board[i] = 0
                removed++
            }
        }
    }
}
