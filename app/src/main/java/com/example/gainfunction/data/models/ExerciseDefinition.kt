package com.example.gainfunction.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a definition of an exercise.
 * This stores both pre-defined exercises and user-created custom exercises.
 */
@Entity(tableName = "exercise_definitions")
data class ExerciseDefinition(
    /**
     * Name of the exercise, used as the primary key
     */
    @PrimaryKey
    val name: String,
    
    /**
     * Flag indicating whether this is a custom user-created exercise (true)
     * or a pre-defined system exercise (false)
     */
    val isCustom: Boolean
) 