package com.example.sudoku.domain.model

data class Cell(
    val value: Int = 0,
    val isFixed: Boolean = false,
    val notes: Set<Int> = emptySet(),
    val isError: Boolean = false
) {
    val isEmpty: Boolean get() = value == 0
}
