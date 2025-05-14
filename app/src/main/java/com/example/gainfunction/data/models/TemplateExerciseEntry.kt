package com.example.gainfunction.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entity class representing an exercise within a workout template.
 * This entity manages the many-to-many relationship between templates and exercises.
 */
@Entity(
    tableName = "template_exercises",
    primaryKeys = ["templateId", "exerciseName"],
    foreignKeys = [
        ForeignKey(
            entity = Template::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("templateId")] // Index for faster queries on foreign key
)
data class TemplateExerciseEntry(
    /**
     * ID of the template this exercise belongs to
     */
    val templateId: Long,
    
    /**
     * Name of the exercise
     */
    val exerciseName: String,
    
    /**
     * Default number of sets for this exercise
     */
    val defaultSets: Int = 3,
    
    /**
     * Default number of reps for this exercise
     */
    val defaultReps: Int = 10,
    
    /**
     * Default weight for this exercise (in whatever unit the user prefers)
     */
    val defaultWeight: Double? = null,
    
    /**
     * Order of this exercise in the template (for display purposes)
     */
    val orderPosition: Int = 0
) 