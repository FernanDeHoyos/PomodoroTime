package com.fernan.pomodorotime.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.fernan.pomodorotime.data.model.PomodoroSession

@Dao
interface PomodoroDao {

    @Insert
    suspend fun insertSession(session: PomodoroSession)

    @Query("SELECT SUM(totalTimeInSeconds) FROM pomodoro_sessions WHERE habitId = :habitId")
    suspend fun getTotalAccumulatedTime(habitId: Int): Int?

    @Query("SELECT * FROM pomodoro_sessions WHERE habitId = :habitId")
    suspend fun getSessionsByHabit(habitId: Int): List<PomodoroSession>

    @Query("""
    SELECT COUNT(*) FROM pomodoro_sessions 
    WHERE habitId = :habitId 
    AND date(timestamp / 1000, 'unixepoch') = date('now')
""")
    suspend fun getTodayPomodorosCount(habitId: Int): Int

}