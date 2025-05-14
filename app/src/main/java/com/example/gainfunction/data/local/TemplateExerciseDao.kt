package com.example.gainfunction.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gainfunction.data.models.TemplateExerciseEntry
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for TemplateExerciseEntry entities.
 * Provides methods to interact with the template_exercises table in the database.
 */
@Dao
interface TemplateExerciseDao {
    
    /**
     * Insert a new template exercise entry into the database
     * @param templateExercise The template exercise entry to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercise(templateExercise: TemplateExerciseEntry)
    
    /**
     * Insert multiple template exercise entries into the database
     * @param templateExercises The list of template exercise entries to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateExercises(templateExercises: List<TemplateExerciseEntry>)
    
    /**
     * Update an existing template exercise entry in the database
     * @param templateExercise The template exercise entry to update
     */
    @Update
    suspend fun updateTemplateExercise(templateExercise: TemplateExerciseEntry)
    
    /**
     * Delete a template exercise entry from the database
     * @param templateExercise The template exercise entry to delete
     */
    @Delete
    suspend fun deleteTemplateExercise(templateExercise: TemplateExerciseEntry)
    
    /**
     * Get all exercise entries for a specific template
     * @param templateId The ID of the template
     * @return A Flow of List of all exercise entries for the template, ordered by position
     */
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderPosition")
    fun getExercisesForTemplate(templateId: Long): Flow<List<TemplateExerciseEntry>>
    
    /**
     * Get an exercise entry for a specific template and exercise name
     * @param templateId The ID of the template
     * @param exerciseName The name of the exercise
     * @return The template exercise entry, or null if not found
     */
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId AND exerciseName = :exerciseName")
    suspend fun getTemplateExercise(templateId: Long, exerciseName: String): TemplateExerciseEntry?
    
    /**
     * Get the count of exercises in a template
     * @param templateId The ID of the template
     * @return The number of exercises in the template
     */
    @Query("SELECT COUNT(*) FROM template_exercises WHERE templateId = :templateId")
    suspend fun getExerciseCountForTemplate(templateId: Long): Int
    
    /**
     * Delete all exercise entries for a specific template
     * @param templateId The ID of the template
     * @return The number of exercise entries deleted
     */
    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteAllExercisesForTemplate(templateId: Long): Int
    
    /**
     * Update the order position of a template exercise
     * @param templateId The ID of the template
     * @param exerciseName The name of the exercise
     * @param newPosition The new order position
     */
    @Query("UPDATE template_exercises SET orderPosition = :newPosition WHERE templateId = :templateId AND exerciseName = :exerciseName")
    suspend fun updateExercisePosition(templateId: Long, exerciseName: String, newPosition: Int)
} 