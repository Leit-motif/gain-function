package com.example.gainfunction.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a workout session.
 */
@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Timestamp for when the workout was performed
     */
    val date: Long,
    
    /**
     * Name of the template used for this workout, if any
     */
    val templateName: String? = null,
    
    /**
     * Duration of the workout in minutes
     */
    val duration: Int? = null,
    
    /**
     * User notes about the workout
     */
    val notes: String? = null
) 