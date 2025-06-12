package com.example.calculadoradenotas.data.curso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculadoradenotas.data.nota.NotaDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CursoViewModel(private val cursoDao: CursoDao): ViewModel() {
    private val _cursos = MutableStateFlow<List<CursoEntity>>(emptyList())
    val cursos: StateFlow<List<CursoEntity>> = _cursos.asStateFlow()

    init {
        viewModelScope.launch {
            cursoDao.getCursos().collect {
                _cursos.value = it
            }
        }
    }

    fun agregarCurso(curso: String, color: Long) {
        viewModelScope.launch {
            val nuevoCurso = CursoEntity(curso = curso, color = color)
            cursoDao.insertCurso(nuevoCurso)
        }
    }

    fun updateTotalNotas(cursoId: Int, nuevoTotal: Int) {
        viewModelScope.launch {
            cursoDao.updateTotalNotas(cursoId, nuevoTotal)
        }
    }

    fun updateObjetivo(cursoId: Int, nuevoObjetivo: Double) {
        viewModelScope.launch {
            cursoDao.updateObjetivo(cursoId, nuevoObjetivo)
        }
    }

    fun updateProgreso(cursoId: Int, nuevoProgreso: Double) {
        viewModelScope.launch {
            cursoDao.updateProgreso(cursoId, nuevoProgreso)
        }
    }

    fun updateName(cursoId: Int, nuevoNombre: String) {
        viewModelScope.launch {
            cursoDao.updateName(cursoId, nuevoNombre)
        }
    }

    fun deleteCurso(curso: CursoEntity) {
        viewModelScope.launch {
            cursoDao.deleteCurso(curso)
        }
    }

    fun getCurso(id: Int?): Flow<CursoEntity> {
        return cursoDao.getCurso(id)
    }
}