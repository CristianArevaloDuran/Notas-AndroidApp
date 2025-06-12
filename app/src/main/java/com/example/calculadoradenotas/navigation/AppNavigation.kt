package com.example.calculadoradenotas.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.calculadoradenotas.screens.Curso.Curso
import com.example.calculadoradenotas.screens.Home.Home

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Home.route,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(500)) { fullWidth -> fullWidth }
        },
        exitTransition = {
            slideOutHorizontally(animationSpec = tween(500)) { fullWidth -> -fullWidth }
        },
        popEnterTransition = {
            slideInHorizontally(animationSpec = tween(500)) { fullWidth -> -fullWidth }
        },
        popExitTransition = {
            slideOutHorizontally(animationSpec = tween(500)) { fullWidth -> fullWidth }
        }
    ) {
        composable(route = AppScreens.Home.route) {
            Home(navController)
        }
        composable(
            route = AppScreens.Curso.route + "/{index}",
            arguments = listOf(navArgument(name = "index") {
                type = NavType.IntType
            })
        ) {
            Curso(navController, it.arguments?.getInt("index"))
        }
    }
}
