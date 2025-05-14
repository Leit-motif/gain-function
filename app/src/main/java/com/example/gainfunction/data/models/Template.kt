package com.example.gainfunction.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a workout template.
 * Templates can be used to quickly start a new workout with a predefined set of exercises.
 */
@Entity(tableName = "templates")
data class Template(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Name of the template
     */
    val name: String,
    
    /**
     * Optional description of the template
     */
    val description: String? = null
) 