package com.example.gainfunction.ui.screens.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fitness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gainfunction.data.models.ExerciseDefinition
import com.example.gainfunction.ui.components.AddExerciseDialog
import com.example.gainfunction.ui.theme.GainFunctionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error or success messages in a snackbar
    LaunchedEffect(uiState.error, uiState.exerciseAddedMessage) {
        uiState.error?.let { error ->
            val result = snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "Dismiss",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                viewModel.clearMessages()
            }
        }
        
        uiState.exerciseAddedMessage?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed || result == SnackbarResult.Dismissed) {
                viewModel.clearMessages()
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Exercise Management") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Exercise") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                onClick = { viewModel.showAddExerciseDialog() },
                expanded = true
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                ExerciseList(
                    exercises = uiState.exercises,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Show the add exercise dialog if requested
            if (uiState.showAddExerciseDialog) {
                AddExerciseDialog(
                    exerciseName = uiState.newExerciseName,
                    isValidExerciseName = uiState.isValidExerciseName,
                    onExerciseNameChange = { viewModel.updateNewExerciseName(it) },
                    onDismiss = { viewModel.hideAddExerciseDialog() },
                    onAddExercise = { viewModel.addCustomExercise() }
                )
            }
        }
    }
}

@Composable
fun ExerciseList(
    exercises: List<ExerciseDefinition>,
    modifier: Modifier = Modifier
) {
    if (exercises.isEmpty()) {
        EmptyExerciseList(modifier)
    } else {
        // Group exercises by custom status for display
        val (customExercises, predefinedExercises) = exercises.partition { it.isCustom }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Custom exercises section
            if (customExercises.isNotEmpty()) {
                item {
                    Text(
                        text = "My Custom Exercises",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(customExercises) { exercise ->
                    ExerciseItem(exercise = exercise)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // Predefined exercises section
            if (predefinedExercises.isNotEmpty()) {
                item {
                    Text(
                        text = "Predefined Exercises",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(predefinedExercises) { exercise ->
                    ExerciseItem(exercise = exercise)
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: ExerciseDefinition,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Fitness,
                contentDescription = null,
                tint = if (exercise.isCustom) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            if (exercise.isCustom) {
                Text(
                    text = "Custom",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyExerciseList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Fitness,
            contentDescription = null,
            modifier = Modifier
                .height(72.dp)
                .width(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No exercises found",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap the 'Add Exercise' button to create a custom exercise",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseScreenPreview() {
    GainFunctionTheme {
        ExerciseScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseItemPreview() {
    GainFunctionTheme {
        ExerciseItem(
            exercise = ExerciseDefinition(name = "Bench Press", isCustom = false)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomExerciseItemPreview() {
    GainFunctionTheme {
        ExerciseItem(
            exercise = ExerciseDefinition(name = "My Custom Exercise", isCustom = true)
        )
    }
} 