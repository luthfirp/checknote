package org.d3if3082.checknote.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.d3if3082.checknote.database.NotesDao
import org.d3if3082.checknote.model.Notes

class MainViewModel(private val dao: NotesDao): ViewModel() {

    val data: StateFlow<List<Notes>> = dao.getNotes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
    fun insert(judul: String, desc: String, kategori: String) {
        val notes = Notes(
            judul = judul,
            desc = desc,
            kategori = kategori
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(notes)
        }
    }

    suspend fun getNotes(id: Long): Notes? {
        return dao.getNotesById(id)
    }

    fun update(id: Long, judul: String, desc: String, kategori: String) {
        val notes = Notes(
            id = id,
            judul = judul,
            desc = desc,
            kategori = kategori
        )

        viewModelScope.launch(Dispatchers.IO) {
            dao.update(notes)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteById(id)
        }
    }
}