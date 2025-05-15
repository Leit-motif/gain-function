package com.example.gainfunction.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gainfunction.data.models.ExerciseDefinition
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ExerciseDefinition entities.
 * Provides methods to interact with the exercise_definitions table in the database.
 */
@Dao
interface ExerciseDefinitionDao {
    
    /**
     * Insert a new exercise definition into the database
     * @param exerciseDefinition The exercise definition to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExerciseDefinition(exerciseDefinition: ExerciseDefinition)
    
    /**
     * Insert multiple exercise definitions into the database
     * @param exerciseDefinitions The list of exercise definitions to insert
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExerciseDefinitions(exerciseDefinitions: List<ExerciseDefinition>)
    
    /**
     * Update an existing exercise definition in the database
     * @param exerciseDefinition The exercise definition to update
     */
    @Update
    suspend fun updateExerciseDefinition(exerciseDefinition: ExerciseDefinition)
    
    /**
     * Delete an exercise definition from the database
     * @param exerciseDefinition The exercise definition to delete
     */
    @Delete
    suspend fun deleteExerciseDefinition(exerciseDefinition: ExerciseDefinition)
    
    /**
     * Get all exercise definitions from the database as a Flow
     * @return A Flow of List of all exercise definitions, ordered by name
     */
    @Query("SELECT * FROM exercise_definitions ORDER BY name")
    fun getAllExerciseDefinitions(): Flow<List<ExerciseDefinition>>
    
    /**
     * Get all custom exercise definitions from the database as a Flow
     * @return A Flow of List of all custom exercise definitions, ordered by name
     */
    @Query("SELECT * FROM exercise_definitions WHERE isCustom = 1 ORDER BY name")
    fun getCustomExerciseDefinitions(): Flow<List<ExerciseDefinition>>
    
    /**
     * Get all pre-defined exercise definitions from the database as a Flow
     * @return A Flow of List of all pre-defined exercise definitions, ordered by name
     */
    @Query("SELECT * FROM exercise_definitions WHERE isCustom = 0 ORDER BY name")
    fun getPredefinedExerciseDefinitions(): Flow<List<ExerciseDefinition>>
    
    /**
     * Get an exercise definition by its name
     * @param name The name of the exercise definition to retrieve
     * @return The exercise definition with the specified name, or null if not found
     */
    @Query("SELECT * FROM exercise_definitions WHERE name = :name LIMIT 1")
    suspend fun getExerciseDefinitionByName(name: String): ExerciseDefinition?
    
    /**
     * Check if an exercise definition exists
     * @param name The name of the exercise definition to check
     * @return True if an exercise definition with the specified name exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM exercise_definitions WHERE name = :name LIMIT 1)")
    suspend fun exerciseDefinitionExists(name: String): Boolean
} 