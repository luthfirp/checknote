package org.d3if3082.checknote.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val judul: String,
    val desc: String,
    val kategori: String,
)