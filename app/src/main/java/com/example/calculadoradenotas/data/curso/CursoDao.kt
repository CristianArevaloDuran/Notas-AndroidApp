package com.example.calculadoradenotas.data.curso

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CursoDao {

    @Insert
    suspend fun insertCurso(curso: CursoEntity)

    @Query("SELECT * FROM CursoEntity")
    fun getCursos(): Flow<List<CursoEntity>>

    @Query("SELECT * FROM CursoEntity WHERE id = :id")
    fun getCurso(id: Int?): Flow<CursoEntity>

    @Query("UPDATE CursoEntity SET totalNotas = :nuevoTotal WHERE id = :cursoId")
    suspend fun updateTotalNotas(cursoId: Int, nuevoTotal: Int)

    @Query("UPDATE CursoEntity SET objetivo = :nuevoObjetivo WHERE id = :cursoId")
    suspend fun updateObjetivo(cursoId: Int, nuevoObjetivo: Double)

    @Query("UPDATE CursoEntity SET progreso = :nuevoProgreso WHERE id = :cursoId")
    suspend fun updateProgreso(cursoId: Int, nuevoProgreso: Double)

    @Query("UPDATE CursoEntity SET curso = :nuevoNombre WHERE id = :cursoId")
    suspend fun updateName(cursoId: Int, nuevoNombre: String)

    @Delete
    suspend fun deleteCurso(curso: CursoEntity)

}