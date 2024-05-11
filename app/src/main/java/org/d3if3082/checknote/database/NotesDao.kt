package org.d3if3082.checknote.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.d3if3082.checknote.model.Notes

@Dao
interface NotesDao {
    @Insert
    suspend fun insert(notes: Notes)

    @Update
    suspend fun update(notes: Notes)

    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getNotes(): Flow<List<Notes>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNotesById(id: Long): Notes?

    @Query("DELETE FROM notes where id = :id")
    suspend fun deleteById(id: Long)
}