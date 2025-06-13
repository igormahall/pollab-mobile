package com.example.enqueteapp.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.enqueteapp.presentation.poll_detail.PollDetailScreen
// Garanta que o EnqueteFormComponent está importado
import com.example.enqueteapp.presentation.poll_form.EnqueteFormComponent
import com.example.enqueteapp.presentation.poll_list.PollListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "poll_list") {

        composable(route = "poll_list") {
            PollListScreen(navController = navController)
        }

        // ROTA DO FORMULÁRIO - Verifique se ela existe no seu arquivo
        composable(route = "enquetes/nova") {
            EnqueteFormComponent(navController = navController)
        }

        composable(
            route = "poll_detail/{pollId}",
            arguments = listOf(navArgument("pollId") { type = NavType.StringType })
        ) { backStackEntry ->
            PollDetailScreen(
                pollId = backStackEntry.arguments?.getString("pollId"),
                navController = navController
            )
        }
    }
}