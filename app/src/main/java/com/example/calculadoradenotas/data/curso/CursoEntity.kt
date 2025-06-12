package com.example.calculadoradenotas.data.curso

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CursoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val curso: String,
    val color: Long,
    val totalNotas: Int = 0,
    val objetivo: Double = 0.0,
    val progreso: Double = 0.0
)
