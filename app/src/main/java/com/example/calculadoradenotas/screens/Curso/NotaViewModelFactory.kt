package com.example.calculadoradenotas.screens.Curso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calculadoradenotas.data.nota.NotaDao
import com.example.calculadoradenotas.data.nota.NotaViewModel

class NotaViewModelFactory(
    private val notaDao: NotaDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotaViewModel::class.java)) {
            return NotaViewModel(notaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}