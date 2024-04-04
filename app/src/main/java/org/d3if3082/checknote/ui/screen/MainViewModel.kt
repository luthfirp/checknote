package org.d3if3082.checknote.ui.screen

import androidx.lifecycle.ViewModel
import org.d3if3082.checknote.model.Note

class MainViewModel : ViewModel() {
    val data = getDataNote()

    private fun getDataNote(): List<Note> {
        val data = mutableListOf<Note>()
        for (i in 1 until 5) {
            data.add(
                Note(
                    i.toLong(),
                    "Bahan Masak",
                    "Pas belanja butuh bla bla bla, terus bla bla, jangan lupa bla bla"
                )
            )
        }
        return data
    }
}