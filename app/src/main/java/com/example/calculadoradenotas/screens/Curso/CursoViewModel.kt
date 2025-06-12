package com.example.calculadoradenotas.screens.Curso

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CursoViewModel: ViewModel() {
    var state by mutableStateOf(CursoState())
        private set
    fun onNotaChange(nota: Float) {
        state = state.copy(
            nota = nota
        )
    }
}