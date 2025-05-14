package com.example.gainfunction.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity class representing a single set within an exercise log.
 * Each exercise can have multiple sets.
 */
@Entity(
    tableName = "set_entries",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLog::class,
            parentColumns = ["id"],
            childColumns = ["exerciseLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseLogId")] // Index for faster queries on foreign key
)
data class SetEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Reference to the parent exercise log
     */
    val exerciseLogId: Long,
    
    /**
     * Set number within the exercise (1, 2, 3, etc.)
     */
    val setNumber: Int,
    
    /**
     * Number of repetitions performed
     */
    val reps: Int,
    
    /**
     * Weight used for the set (in whatever unit the user prefers)
     */
    val weight: Double,
    
    /**
     * Rest time in seconds after this set
     */
    val restTimeSeconds: Int? = null
) 