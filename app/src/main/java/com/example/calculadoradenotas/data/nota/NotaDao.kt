package com.example.calculadoradenotas.data.nota

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Query("SELECT * FROM NotaEntity WHERE cursoId = :cursoId")
    fun getNotas(cursoId: Int?): Flow<List<NotaEntity>>

    @Insert
    suspend fun insertNota(nota: NotaEntity)

    @Query("UPDATE NotaEntity SET calificacion = :nuevaNota WHERE id = :notaId")
    suspend fun updateNota(notaId: Int, nuevaNota: Double)

    @Delete
    suspend fun deleteNota(nota: NotaEntity)
}