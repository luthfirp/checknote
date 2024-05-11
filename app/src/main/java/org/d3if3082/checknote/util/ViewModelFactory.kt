package org.d3if3082.checknote.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.d3if3082.checknote.database.NotesDao
import org.d3if3082.checknote.ui.screen.MainViewModel

class ViewModelFactory (private val dao: NotesDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dao) as T
        } else if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}