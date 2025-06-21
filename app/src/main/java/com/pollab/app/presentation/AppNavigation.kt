package com.example.app.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pollab.app.R
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app.presentation.poll_detail.PollDetailScreen
import com.example.app.presentation.poll_form.EnqueteFormComponent
import com.example.app.presentation.poll_list.PollListScreen

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

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.pollab_logo),
            contentDescription = "Logo Pollab",
            modifier = Modifier
                .size(180.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pollab",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
    }
}
