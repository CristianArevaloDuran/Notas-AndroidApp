package com.example.calculadoradenotas.data.nota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotaViewModel(private val notaDao: NotaDao): ViewModel() {
    private val _notas = MutableStateFlow<List<NotaEntity>>(emptyList())
    val notas: StateFlow<List<NotaEntity>> = _notas.asStateFlow()


    fun getNotas(cursoId: Int?) {
        viewModelScope.launch {
            notaDao.getNotas(cursoId).collect {
                _notas.value = it
            }
        }
    }

    fun agregarNota(cursoId: Int, calificacion: Double) {
        viewModelScope.launch {
            val nota = NotaEntity(cursoId = cursoId, calificacion = calificacion)
            notaDao.insertNota(nota)
        }
    }

    fun updateNota(notaId: Int, nuevaNota: Double) {
        viewModelScope.launch {
            notaDao.updateNota(notaId, nuevaNota)
        }
    }

    fun deleteNota(nota: NotaEntity) {
        viewModelScope.launch {
            notaDao.deleteNota(nota)
        }
    }
}