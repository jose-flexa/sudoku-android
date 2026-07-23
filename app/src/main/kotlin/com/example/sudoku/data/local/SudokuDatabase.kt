package com.example.sudoku.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.example.sudoku.data.local.dao.GameDao
import com.example.sudoku.data.local.entity.GameEntity

@Database(entities = [GameEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
