package com.example.sudoku.data.local

import androidx.room.TypeConverter
import com.example.sudoku.domain.model.Difficulty
import com.example.sudoku.domain.model.GameStatus

class Converters {
    @TypeConverter
    fun fromDifficulty(value: Difficulty) = value.name

    @TypeConverter
    fun toDifficulty(value: String) = Difficulty.valueOf(value)

    @TypeConverter
    fun fromGameStatus(value: GameStatus) = value.name

    @TypeConverter
    fun toGameStatus(value: String) = GameStatus.valueOf(value)
}
