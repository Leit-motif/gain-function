package com.example.gainfunction.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gainfunction.data.models.SetEntry
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for SetEntry entities.
 * Provides methods to interact with the set_entries table in the database.
 */
@Dao
interface SetEntryDao {
    
    /**
     * Insert a new set entry into the database
     * @param setEntry The set entry to insert
     * @return The ID of the newly inserted set entry
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetEntry(setEntry: SetEntry): Long
    
    /**
     * Insert multiple set entries into the database
     * @param setEntries The list of set entries to insert
     * @return The list of IDs of the newly inserted set entries
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetEntries(setEntries: List<SetEntry>): List<Long>
    
    /**
     * Update an existing set entry in the database
     * @param setEntry The set entry to update
     */
    @Update
    suspend fun updateSetEntry(setEntry: SetEntry)
    
    /**
     * Delete a set entry from the database
     * @param setEntry The set entry to delete
     */
    @Delete
    suspend fun deleteSetEntry(setEntry: SetEntry)
    
    /**
     * Get a set entry by its ID
     * @param setEntryId The ID of the set entry to retrieve
     * @return The set entry with the specified ID, or null if not found
     */
    @Query("SELECT * FROM set_entries WHERE id = :setEntryId")
    suspend fun getSetEntryById(setEntryId: Long): SetEntry?
    
    /**
     * Get all set entries for a specific exercise log
     * @param exerciseLogId The ID of the exercise log
     * @return A Flow of List of all set entries for the exercise log, ordered by set number
     */
    @Query("SELECT * FROM set_entries WHERE exerciseLogId = :exerciseLogId ORDER BY setNumber")
    fun getSetEntriesForExerciseLog(exerciseLogId: Long): Flow<List<SetEntry>>
    
    /**
     * Get the count of sets for an exercise log
     * @param exerciseLogId The ID of the exercise log
     * @return The number of sets in the exercise log
     */
    @Query("SELECT COUNT(*) FROM set_entries WHERE exerciseLogId = :exerciseLogId")
    suspend fun getSetCountForExerciseLog(exerciseLogId: Long): Int
    
    /**
     * Delete all set entries for an exercise log
     * @param exerciseLogId The ID of the exercise log
     * @return The number of set entries deleted
     */
    @Query("DELETE FROM set_entries WHERE exerciseLogId = :exerciseLogId")
    suspend fun deleteAllSetsForExerciseLog(exerciseLogId: Long): Int
} 