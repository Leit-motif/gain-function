package com.example.gainfunction.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity class representing a logged exercise within a workout.
 * Each workout can have multiple exercise logs.
 */
@Entity(
    tableName = "exercise_logs",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")] // Index for faster queries on foreign key
)
data class ExerciseLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Reference to the parent workout
     */
    val workoutId: Long,
    
    /**
     * Name of the exercise
     */
    val exerciseName: String,
    
    /**
     * User notes specific to this exercise
     */
    val notes: String? = null
) 