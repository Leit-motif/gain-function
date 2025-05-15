package com.example.gainfunction.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gainfunction.ui.theme.GainFunctionTheme

/**
 * A dialog for adding a new custom exercise
 *
 * @param exerciseName The current input value for the exercise name
 * @param isValidExerciseName Whether the current exercise name is valid
 * @param onExerciseNameChange Callback when the exercise name changes
 * @param onDismiss Callback when the dialog is dismissed
 * @param onAddExercise Callback when the add button is clicked
 */
@Composable
fun AddExerciseDialog(
    exerciseName: String,
    isValidExerciseName: Boolean,
    onExerciseNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddExercise: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Custom Exercise",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Enter the name of your custom exercise:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = onExerciseNameChange,
                    label = { Text("Exercise Name") },
                    isError = exerciseName.isNotEmpty() && !isValidExerciseName,
                    supportingText = {
                        if (exerciseName.isNotEmpty() && !isValidExerciseName) {
                            Text(
                                text = "Name must be between 1-50 characters",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "This exercise will be added to your custom exercises list.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onAddExercise,
                enabled = isValidExerciseName
            ) {
                Text("Add Exercise")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddExerciseDialogPreview() {
    GainFunctionTheme {
        AddExerciseDialog(
            exerciseName = "My Custom Exercise",
            isValidExerciseName = true,
            onExerciseNameChange = {},
            onDismiss = {},
            onAddExercise = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddExerciseDialogErrorPreview() {
    GainFunctionTheme {
        AddExerciseDialog(
            exerciseName = "",
            isValidExerciseName = false,
            onExerciseNameChange = {},
            onDismiss = {},
            onAddExercise = {}
        )
    }
} 