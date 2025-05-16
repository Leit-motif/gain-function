package com.example.gainfunction.ui.screens.template

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gainfunction.data.models.ExerciseDefinition
import com.example.gainfunction.data.models.Template
import com.example.gainfunction.data.models.TemplateExerciseEntry
import com.example.gainfunction.domain.usecases.ExerciseUseCases
import com.example.gainfunction.domain.usecases.TemplateUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for the Template List screen
 */
data class TemplateListUiState(
    val templates: List<Template> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddTemplateDialog: Boolean = false,
    val newTemplateName: String = "",
    val newTemplateDescription: String = "",
    val isValidTemplateName: Boolean = false,
    val templateAddedMessage: String? = null
)

/**
 * UI State for the Template Detail screen
 */
data class TemplateDetailUiState(
    val template: Template? = null,
    val exercises: List<TemplateExerciseEntry> = emptyList(),
    val availableExercises: List<ExerciseDefinition> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val templateName: String = "",
    val templateDescription: String = "",
    val isValidTemplateName: Boolean = false,
    val showExercisePickerDialog: Boolean = false,
    val exerciseToAdd: String? = null,
    val defaultSets: Int = 3,
    val defaultReps: Int = 8,
    val defaultWeight: Double? = null,
    val saveMessage: String? = null
)

/**
 * ViewModel for template management functionality
 */
@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val templateUseCases: TemplateUseCases,
    private val exerciseUseCases: ExerciseUseCases
) : ViewModel() {

    // Template List State
    private val _listUiState = MutableStateFlow(TemplateListUiState())
    val listUiState: StateFlow<TemplateListUiState> = _listUiState.asStateFlow()

    // Template Detail State
    private val _detailUiState = MutableStateFlow(TemplateDetailUiState())
    val detailUiState: StateFlow<TemplateDetailUiState> = _detailUiState.asStateFlow()

    // Selected Template ID
    private val _selectedTemplateId = MutableStateFlow<Long?>(null)
    private var observeDetailJob: Job? = null
    
    init {
        loadTemplates()
        loadAvailableExercises()
    }

    /**
     * Load all templates from the database
     */
    private fun loadTemplates() {
        viewModelScope.launch {
            _listUiState.update { it.copy(isLoading = true, error = null) }
            try {
                templateUseCases.getAllTemplates().collect { templates ->
                    _listUiState.update { it.copy(templates = templates, isLoading = false) }
                }
            } catch (e: Exception) {
                _listUiState.update { it.copy(error = "Failed to load templates: ${e.message}", isLoading = false) }
            }
        }
    }
    
    /**
     * Load available exercises for adding to templates
     */
    private fun loadAvailableExercises() {
        viewModelScope.launch {
            try {
                exerciseUseCases.getAllExercises().collect { exercises ->
                    _detailUiState.update { it.copy(availableExercises = exercises) }
                }
            } catch (e: Exception) {
                _detailUiState.update { it.copy(error = "Failed to load exercises: ${e.message}") }
            }
        }
    }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSelectedTemplate() {
        observeDetailJob?.cancel()
        
        // Set loading state
        _detailUiState.update { it.copy(isLoading = true, error = null) }
        
        observeDetailJob = viewModelScope.launch {
            val templateId = _selectedTemplateId.value
            if (templateId == null) {
                _detailUiState.update { 
                    it.copy(
                        template = null,
                        exercises = emptyList(),
                        isLoading = false
                    )
                }
                return@launch
            }
            
            // Create a flow for the template
            val templateFlow = templateUseCases.getTemplateById(templateId)?.let { template ->
                flowOf(template)
            } ?: flowOf(null)
            
            // Create a flow for the exercises
            val exercisesFlow = templateUseCases.getExercisesForTemplate(templateId)
            
            // Combine the flows and collect the result
            combine(
                templateFlow,
                exercisesFlow
            ) { template, exercises ->
                _detailUiState.update { 
                    it.copy(
                        template = template,
                        exercises = exercises,
                        templateName = template?.name ?: "",
                        templateDescription = template?.description ?: "",
                        isValidTemplateName = !template?.name.isNullOrBlank(),
                        isLoading = false,
                        error = null
                    )
                }
            }
            .catch { e -> 
                _detailUiState.update { 
                    it.copy(
                        error = "Error loading template: ${e.message}",
                        isLoading = false
                    )
                }
            }
            .collect { /* Just collecting triggers the flow */ }
        }
    }
    
    /**
     * Select a template to view/edit
     * @param templateId The ID of the template to select
     */
    fun selectTemplate(templateId: Long?) {
        // Cancel any existing observation
        observeDetailJob?.cancel()
        _selectedTemplateId.value = templateId
        
        if (templateId == null) {
            // Creating a new template, clear detail state
            _detailUiState.update {
                it.copy(
                    template = null,
                    exercises = emptyList(),
                    templateName = "",
                    templateDescription = "",
                    isValidTemplateName = false,
                    isLoading = false,
                    error = null
                )
            }
        } else {
            // For existing template, start observing
            observeSelectedTemplate()
        }
    }
    
    // Template List Screen Methods
    
    /**
     * Update the new template name in the list UI state
     */
    fun updateNewTemplateName(name: String) {
        _listUiState.update { 
            it.copy(
                newTemplateName = name,
                isValidTemplateName = isValidTemplateName(name),
                templateAddedMessage = null
            ) 
        }
    }
    
    /**
     * Update the new template description in the list UI state
     */
    fun updateNewTemplateDescription(description: String) {
        _listUiState.update { 
            it.copy(
                newTemplateDescription = description
            ) 
        }
    }
    
    /**
     * Show the add template dialog
     */
    fun showAddTemplateDialog() {
        _listUiState.update { it.copy(showAddTemplateDialog = true) }
    }
    
    /**
     * Hide the add template dialog
     */
    fun hideAddTemplateDialog() {
        _listUiState.update { 
            it.copy(
                showAddTemplateDialog = false,
                newTemplateName = "",
                newTemplateDescription = "",
                isValidTemplateName = false
            ) 
        }
    }
    
    /**
     * Create a new template
     */
    fun createTemplate(): Long? {
        val templateName = _listUiState.value.newTemplateName.trim()
        val templateDescription = _listUiState.value.newTemplateDescription.trim().ifEmpty { null }
        
        if (!isValidTemplateName(templateName)) {
            return null
        }
        
        var newTemplateId: Long? = null
        
        viewModelScope.launch {
            try {
                newTemplateId = templateUseCases.createTemplate(templateName, templateDescription)
                
                _listUiState.update { 
                    it.copy(
                        showAddTemplateDialog = false,
                        newTemplateName = "",
                        newTemplateDescription = "",
                        isValidTemplateName = false,
                        templateAddedMessage = "Template '$templateName' created successfully"
                    ) 
                }
            } catch (e: Exception) {
                _listUiState.update { 
                    it.copy(
                        error = "Failed to create template: ${e.message}"
                    ) 
                }
            }
        }
        
        return newTemplateId
    }
    
    /**
     * Delete a template
     */
    fun deleteTemplate(template: Template) {
        viewModelScope.launch {
            try {
                templateUseCases.deleteTemplate(template)
                
                _listUiState.update { 
                    it.copy(
                        templateAddedMessage = "Template '${template.name}' deleted"
                    ) 
                }
                
                // If we were viewing this template, clear the selection
                if (_selectedTemplateId.value == template.id) {
                    selectTemplate(null)
                }
            } catch (e: Exception) {
                _listUiState.update { 
                    it.copy(
                        error = "Failed to delete template: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Clear list UI messages
     */
    fun clearListMessages() {
        _listUiState.update { 
            it.copy(
                error = null,
                templateAddedMessage = null
            ) 
        }
    }
    
    // Template Detail Screen Methods
    
    /**
     * Update the template name in the detail UI state
     */
    fun updateTemplateName(name: String) {
        _detailUiState.update { 
            it.copy(
                templateName = name,
                isValidTemplateName = isValidTemplateName(name),
                saveMessage = null
            ) 
        }
    }
    
    /**
     * Update the template description in the detail UI state
     */
    fun updateTemplateDescription(description: String) {
        _detailUiState.update { 
            it.copy(
                templateDescription = description,
                saveMessage = null
            ) 
        }
    }
    
    /**
     * Save template changes
     */
    fun saveTemplate() {
        val currentTemplate = _detailUiState.value.template
        val templateName = _detailUiState.value.templateName.trim()
        val templateDescription = _detailUiState.value.templateDescription.trim().ifEmpty { null }
        
        if (!isValidTemplateName(templateName)) {
            return
        }
        
        viewModelScope.launch {
            try {
                if (currentTemplate == null) {
                    // Create new template
                    val newTemplateId = templateUseCases.createTemplate(templateName, templateDescription)
                    selectTemplate(newTemplateId)
                    
                    _detailUiState.update { 
                        it.copy(
                            saveMessage = "Template created successfully"
                        ) 
                    }
                } else {
                    // Update existing template
                    val updatedTemplate = currentTemplate.copy(
                        name = templateName,
                        description = templateDescription
                    )
                    templateUseCases.updateTemplate(updatedTemplate)
                    
                    _detailUiState.update { 
                        it.copy(
                            saveMessage = "Template updated successfully"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _detailUiState.update { 
                    it.copy(
                        error = "Failed to save template: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Show the exercise picker dialog
     */
    fun showExercisePickerDialog() {
        _detailUiState.update { it.copy(showExercisePickerDialog = true) }
    }
    
    /**
     * Hide the exercise picker dialog
     */
    fun hideExercisePickerDialog() {
        _detailUiState.update { 
            it.copy(
                showExercisePickerDialog = false,
                exerciseToAdd = null
            ) 
        }
    }
    
    /**
     * Select an exercise to add to the template
     */
    fun selectExerciseToAdd(exerciseName: String) {
        _detailUiState.update { it.copy(exerciseToAdd = exerciseName) }
    }
    
    /**
     * Update default sets value
     */
    fun updateDefaultSets(sets: Int) {
        _detailUiState.update { it.copy(defaultSets = sets) }
    }
    
    /**
     * Update default reps value
     */
    fun updateDefaultReps(reps: Int) {
        _detailUiState.update { it.copy(defaultReps = reps) }
    }
    
    /**
     * Update default weight value
     */
    fun updateDefaultWeight(weight: Double?) {
        _detailUiState.update { it.copy(defaultWeight = weight) }
    }
    
    /**
     * Add selected exercise to template
     */
    fun addExerciseToTemplate() {
        val templateId = _selectedTemplateId.value ?: return
        val exerciseName = _detailUiState.value.exerciseToAdd ?: return
        val defaultSets = _detailUiState.value.defaultSets
        val defaultReps = _detailUiState.value.defaultReps
        val defaultWeight = _detailUiState.value.defaultWeight
        
        // Verify that a weight has been provided (required field)
        if (defaultWeight == null || defaultWeight <= 0) {
            _detailUiState.update { 
                it.copy(
                    error = "Please enter a weight value"
                ) 
            }
            return
        }
        
        viewModelScope.launch {
            try {
                val success = templateUseCases.addExerciseToTemplate(
                    templateId = templateId,
                    exerciseName = exerciseName,
                    defaultSets = defaultSets,
                    defaultReps = defaultReps,
                    defaultWeight = defaultWeight // Now guaranteed to be non-null
                )
                
                if (success) {
                    _detailUiState.update { 
                        it.copy(
                            showExercisePickerDialog = false,
                            exerciseToAdd = null,
                            defaultWeight = null, // Reset default weight to ensure fresh entry next time
                            saveMessage = "Exercise added to template"
                        ) 
                    }
                } else {
                    _detailUiState.update { 
                        it.copy(
                            error = "This exercise is already in the template"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _detailUiState.update { 
                    it.copy(
                        error = "Failed to add exercise: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Remove an exercise from the template
     */
    fun removeExerciseFromTemplate(exerciseName: String) {
        val templateId = _selectedTemplateId.value ?: return
        
        viewModelScope.launch {
            try {
                templateUseCases.removeExerciseFromTemplate(templateId, exerciseName)
                
                _detailUiState.update { 
                    it.copy(
                        saveMessage = "Exercise removed from template"
                    ) 
                }
            } catch (e: Exception) {
                _detailUiState.update { 
                    it.copy(
                        error = "Failed to remove exercise: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Update a template exercise
     */
    fun updateTemplateExercise(templateExercise: TemplateExerciseEntry) {
        viewModelScope.launch {
            try {
                templateUseCases.updateTemplateExercise(templateExercise)
                
                _detailUiState.update { 
                    it.copy(
                        saveMessage = "Exercise updated"
                    ) 
                }
            } catch (e: Exception) {
                _detailUiState.update { 
                    it.copy(
                        error = "Failed to update exercise: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    /**
     * Clear detail UI messages
     */
    fun clearDetailMessages() {
        _detailUiState.update { 
            it.copy(
                error = null,
                saveMessage = null
            ) 
        }
    }
    
    /**
     * Validate the template name
     */
    private fun isValidTemplateName(name: String): Boolean {
        val trimmedName = name.trim()
        // Template name should not be empty and should have a reasonable length
        return trimmedName.isNotEmpty() && trimmedName.length <= 50
    }
} 