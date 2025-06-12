package com.example.calculadoradenotas.screens.Curso

data class CursoState(
    val nota: Float = 0f,
    val notas: List<Float> = emptyList()
)