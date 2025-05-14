package com.example.gainfunction.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gainfunction.data.models.Workout
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Workout entities.
 * Provides methods to interact with the workouts table in the database.
 */
@Dao
interface WorkoutDao {
    
    /**
     * Insert a new workout into the database
     * @param workout The workout to insert
     * @return The ID of the newly inserted workout
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long
    
    /**
     * Insert multiple workouts into the database
     * @param workouts The list of workouts to insert
     * @return The list of IDs of the newly inserted workouts
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<Workout>): List<Long>
    
    /**
     * Update an existing workout in the database
     * @param workout The workout to update
     */
    @Update
    suspend fun updateWorkout(workout: Workout)
    
    /**
     * Delete a workout from the database
     * @param workout The workout to delete
     */
    @Delete
    suspend fun deleteWorkout(workout: Workout)
    
    /**
     * Get all workouts from the database as a Flow
     * @return A Flow of List of all workouts, ordered by date descending (newest first)
     */
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>
    
    /**
     * Get a workout by its ID
     * @param workoutId The ID of the workout to retrieve
     * @return The workout with the specified ID, or null if not found
     */
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Long): Workout?
    
    /**
     * Get all workouts from a specific date range
     * @param startDate The start timestamp (inclusive)
     * @param endDate The end timestamp (inclusive)
     * @return A list of workouts within the date range, ordered by date descending
     */
    @Query("SELECT * FROM workouts WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getWorkoutsInDateRange(startDate: Long, endDate: Long): List<Workout>
} 