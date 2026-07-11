package com.example.sudoku.domain.model

data class Board(
    val cells: List<Cell> = List(81) { Cell() }
) {
    operator fun get(row: Int, col: Int): Cell = cells[row * 9 + col]
    
    fun withCell(row: Int, col: Int, transform: (Cell) -> Cell): Board {
        val newCells = cells.toMutableList()
        val index = row * 9 + col
        newCells[index] = transform(newCells[index])
        return Board(newCells)
    }
}
