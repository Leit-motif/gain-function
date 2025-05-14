package com.example.gainfunction.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gainfunction.data.models.ExerciseLog
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ExerciseLog entities.
 * Provides methods to interact with the exercise_logs table in the database.
 */
@Dao
interface ExerciseLogDao {
    
    /**
     * Insert a new exercise log into the database
     * @param exerciseLog The exercise log to insert
     * @return The ID of the newly inserted exercise log
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(exerciseLog: ExerciseLog): Long
    
    /**
     * Insert multiple exercise logs into the database
     * @param exerciseLogs The list of exercise logs to insert
     * @return The list of IDs of the newly inserted exercise logs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLogs(exerciseLogs: List<ExerciseLog>): List<Long>
    
    /**
     * Update an existing exercise log in the database
     * @param exerciseLog The exercise log to update
     */
    @Update
    suspend fun updateExerciseLog(exerciseLog: ExerciseLog)
    
    /**
     * Delete an exercise log from the database
     * @param exerciseLog The exercise log to delete
     */
    @Delete
    suspend fun deleteExerciseLog(exerciseLog: ExerciseLog)
    
    /**
     * Get an exercise log by its ID
     * @param exerciseLogId The ID of the exercise log to retrieve
     * @return The exercise log with the specified ID, or null if not found
     */
    @Query("SELECT * FROM exercise_logs WHERE id = :exerciseLogId")
    suspend fun getExerciseLogById(exerciseLogId: Long): ExerciseLog?
    
    /**
     * Get all exercise logs for a specific workout
     * @param workoutId The ID of the workout
     * @return A Flow of List of all exercise logs for the workout
     */
    @Query("SELECT * FROM exercise_logs WHERE workoutId = :workoutId")
    fun getExerciseLogsForWorkout(workoutId: Long): Flow<List<ExerciseLog>>
    
    /**
     * Get the count of exercises in a workout
     * @param workoutId The ID of the workout
     * @return The number of exercises in the workout
     */
    @Query("SELECT COUNT(*) FROM exercise_logs WHERE workoutId = :workoutId")
    suspend fun getExerciseCountForWorkout(workoutId: Long): Int
    
    /**
     * Get all exercise logs for a specific exercise name across all workouts
     * @param exerciseName The name of the exercise
     * @return A list of all exercise logs for the exercise, ordered by date of the associated workout (DESC)
     */
    @Query("""
        SELECT e.* FROM exercise_logs e
        INNER JOIN workouts w ON e.workoutId = w.id
        WHERE e.exerciseName = :exerciseName
        ORDER BY w.date DESC
    """)
    suspend fun getExerciseLogsByExerciseName(exerciseName: String): List<ExerciseLog>
} 