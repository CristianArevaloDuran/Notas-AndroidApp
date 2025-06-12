package com.example.calculadoradenotas.screens.Home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class HomeViewModel: ViewModel() {
    var state by mutableStateOf(HomeState())
        private set

    fun onCursoChange(curso: String) {
        state = state.copy(
            curso = curso
        )
    }
}