package com.example.calculadoradenotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.calculadoradenotas.navigation.AppNavigation
import com.example.calculadoradenotas.ui.theme.CalculadoraDeNotasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraDeNotasTheme {
                AppNavigation()
            }
        }
    }
}
