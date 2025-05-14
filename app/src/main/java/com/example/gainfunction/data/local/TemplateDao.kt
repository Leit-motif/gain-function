package com.example.gainfunction.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gainfunction.data.models.Template
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Template entities.
 * Provides methods to interact with the templates table in the database.
 */
@Dao
interface TemplateDao {
    
    /**
     * Insert a new template into the database
     * @param template The template to insert
     * @return The ID of the newly inserted template
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: Template): Long
    
    /**
     * Insert multiple templates into the database
     * @param templates The list of templates to insert
     * @return The list of IDs of the newly inserted templates
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<Template>): List<Long>
    
    /**
     * Update an existing template in the database
     * @param template The template to update
     */
    @Update
    suspend fun updateTemplate(template: Template)
    
    /**
     * Delete a template from the database
     * @param template The template to delete
     */
    @Delete
    suspend fun deleteTemplate(template: Template)
    
    /**
     * Get all templates from the database as a Flow
     * @return A Flow of List of all templates, ordered by name
     */
    @Query("SELECT * FROM templates ORDER BY name")
    fun getAllTemplates(): Flow<List<Template>>
    
    /**
     * Get a template by its ID
     * @param templateId The ID of the template to retrieve
     * @return The template with the specified ID, or null if not found
     */
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: Long): Template?
    
    /**
     * Search for templates by name (case-insensitive partial match)
     * @param searchQuery The search query
     * @return A list of templates that match the search query
     */
    @Query("SELECT * FROM templates WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name")
    suspend fun searchTemplatesByName(searchQuery: String): List<Template>
} 