package com.example.gainfunction.domain.usecases

import com.example.gainfunction.data.local.ExerciseDefinitionDao
import com.example.gainfunction.data.models.ExerciseDefinition
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing exercise definitions
 */
class ExerciseUseCases @Inject constructor(
    private val exerciseDefinitionDao: ExerciseDefinitionDao
) {
    /**
     * Get all exercise definitions
     * @return Flow of List of all exercise definitions
     */
    fun getAllExercises(): Flow<List<ExerciseDefinition>> {
        return exerciseDefinitionDao.getAllExerciseDefinitions()
    }
    
    /**
     * Get only custom exercises created by the user
     * @return Flow of List of custom exercise definitions
     */
    fun getCustomExercises(): Flow<List<ExerciseDefinition>> {
        return exerciseDefinitionDao.getCustomExerciseDefinitions()
    }
    
    /**
     * Get only predefined exercises
     * @return Flow of List of predefined exercise definitions
     */
    fun getPredefinedExercises(): Flow<List<ExerciseDefinition>> {
        return exerciseDefinitionDao.getPredefinedExerciseDefinitions()
    }
    
    /**
     * Add a new custom exercise
     * @param name The name of the exercise to add
     * @return true if the exercise was added successfully, false if it already exists
     */
    suspend fun addCustomExercise(name: String): Boolean {
        // Check if an exercise with the same name already exists
        if (exerciseDefinitionDao.exerciseDefinitionExists(name)) {
            return false
        }
        
        // Create a new exercise definition with the provided name and isCustom=true
        val exerciseDefinition = ExerciseDefinition(
            name = name.trim(),
            isCustom = true
        )
        
        // Insert the exercise into the database
        exerciseDefinitionDao.insertExerciseDefinition(exerciseDefinition)
        return true
    }
} 