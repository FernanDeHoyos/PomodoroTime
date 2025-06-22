package com.fernan.pomodorotime.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fernan.pomodorotime.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("SELECT COUNT(*) FROM habit_completion WHERE habitId = :habitId AND date = :date")
    suspend fun isHabitCompletedToday(habitId: Int, date: String): Int

    @Query("SELECT * FROM habit_completion WHERE habitId = :habitId")
    fun getCompletionsForHabit(habitId: Int): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completion WHERE habitId = :habitId AND date = :date LIMIT 1")
    suspend fun getCompletionByDate(habitId: Int, date: String): HabitCompletion?

    @Query("SELECT * FROM habit_completion WHERE date = :date")
    suspend fun getCompletionsForDate(date: String): List<HabitCompletion>
}