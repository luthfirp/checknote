package org.d3if3082.checknote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.d3if3082.checknote.ui.screen.AddNoteScreen
import org.d3if3082.checknote.ui.screen.InfoScreen
import org.d3if3082.checknote.ui.screen.MainScreen

@Composable
fun SetupNavGraoh(navController: NavHostController = rememberNavController()){
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable(route = Screen.Info.route) {
            InfoScreen(navController)
        }
        composable(route = Screen.Add.route) {
            AddNoteScreen(navController)
        }
    }
}