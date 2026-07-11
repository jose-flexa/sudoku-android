package com.example.sudoku.di

import android.content.Context
import androidx.room.Room
import com.example.sudoku.data.local.SudokuDatabase
import com.example.sudoku.data.local.dao.GameDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SudokuDatabase {
        return Room.databaseBuilder(
            context,
            SudokuDatabase::class.java,
            "sudoku.db"
        ).build()
    }

    @Provides
    fun provideGameDao(database: SudokuDatabase): GameDao {
        return database.gameDao()
    }
}
