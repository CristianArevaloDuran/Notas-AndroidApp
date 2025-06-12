package com.example.calculadoradenotas.screens.Curso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calculadoradenotas.data.curso.CursoDao
import com.example.calculadoradenotas.data.curso.CursoViewModel
import com.example.calculadoradenotas.data.nota.NotaDao

class CursoViewModelFactory(
    private val cursoDao: CursoDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CursoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CursoViewModel(cursoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
