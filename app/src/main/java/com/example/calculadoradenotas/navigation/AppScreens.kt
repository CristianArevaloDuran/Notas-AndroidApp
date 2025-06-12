package com.example.calculadoradenotas.navigation

sealed class AppScreens(val route: String) {
    object Home: AppScreens("home")
    object Curso: AppScreens("detalles-curso")
}