package com.example.calculadoradenotas.data.nota

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.calculadoradenotas.data.curso.CursoEntity

@Entity(
    foreignKeys = [ForeignKey(
        entity = CursoEntity::class,
        parentColumns =  ["id"],
        childColumns = ["cursoId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("cursoId")]
)
data class NotaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cursoId: Int,
    val calificacion: Double
)