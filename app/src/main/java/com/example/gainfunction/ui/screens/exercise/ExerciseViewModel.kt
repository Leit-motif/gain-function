package com.example.gainfunction.ui.screens.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gainfunction.data.models.ExerciseDefinition
import com.example.gainfunction.domain.usecases.ExerciseUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the Exercise screen
 */
data class ExerciseUiState(
    val exercises: List<ExerciseDefinition> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddExerciseDialog: Boolean = false,
    val newExerciseName: String = "",
    val isValidExerciseName: Boolean = false,
    val exerciseAddedMessage: String? = null
)

/**
 * ViewModel for exercise management functionality
 */
@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    init {
        loadExercises()
    }

    /**
     * Load all exercises from the database
     */
    private fun loadExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                exerciseUseCases.getAllExercises().collect { exercises ->
                    _uiState.update { it.copy(exercises = exercises, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load exercises: ${e.message}", isLoading = false) }
            }
        }
    }

    /**
     * Update the new exercise name in the UI state
     */
    fun updateNewExerciseName(name: String) {
        _uiState.update { 
            it.copy(
                newExerciseName = name,
                isValidExerciseName = isValidExerciseName(name),
                exerciseAddedMessage = null  // Clear any previous messages
            ) 
        }
    }

    /**
     * Show the add exercise dialog
     */
    fun showAddExerciseDialog() {
        _uiState.update { it.copy(showAddExerciseDialog = true) }
    }

    /**
     * Hide the add exercise dialog
     */
    fun hideAddExerciseDialog() {
        _uiState.update { 
            it.copy(
                showAddExerciseDialog = false,
                newExerciseName = "",  // Clear the input
                isValidExerciseName = false
            ) 
        }
    }

    /**
     * Add a new custom exercise
     */
    fun addCustomExercise() {
        val exerciseName = _uiState.value.newExerciseName.trim()
        
        if (!isValidExerciseName(exerciseName)) {
            return
        }

        viewModelScope.launch {
            try {
                val success = exerciseUseCases.addCustomExercise(exerciseName)
                
                if (success) {
                    _uiState.update { 
                        it.copy(
                            showAddExerciseDialog = false,
                            newExerciseName = "",  // Clear the input
                            isValidExerciseName = false,
                            exerciseAddedMessage = "Exercise '$exerciseName' added successfully"
                        ) 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            error = "An exercise with this name already exists"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to add exercise: ${e.message}"
                    ) 
                }
            }
        }
    }

    /**
     * Clear any error or success messages
     */
    fun clearMessages() {
        _uiState.update { 
            it.copy(
                error = null,
                exerciseAddedMessage = null
            ) 
        }
    }

    /**
     * Validate the exercise name
     */
    private fun isValidExerciseName(name: String): Boolean {
        val trimmedName = name.trim()
        // Exercise name should not be empty and should have a reasonable length
        return trimmedName.isNotEmpty() && trimmedName.length <= 50
    }
} 