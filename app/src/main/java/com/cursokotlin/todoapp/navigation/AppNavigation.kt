package com.cursokotlin.todoapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cursokotlin.todoapp.addtasks.ui.FirstScreen
import com.cursokotlin.todoapp.addtasks.ui.SecondScreen
import com.cursokotlin.todoapp.addtasks.ui.TasksViewModel

@Composable
fun AppNavigation(tasksViewModel: TasksViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppsScreens.FirstScreen.route) {
        composable(route = AppsScreens.FirstScreen.route) {
            FirstScreen(tasksViewModel, navController)
        }
        composable(route = AppsScreens.SecondScreen.route) {
            SecondScreen(navController)
        }
    }
}