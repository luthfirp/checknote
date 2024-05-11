package org.d3if3082.checknote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.d3if3082.checknote.ui.screen.DetailScreen
import org.d3if3082.checknote.ui.screen.InfoScreen
import org.d3if3082.checknote.ui.screen.KEY_ID_NOTES
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
        composable(route = Screen.FormBaru.route) {
            DetailScreen(navController)
        }
        composable(
            route = Screen.FormUbah.route,
            arguments = listOf(
                navArgument(KEY_ID_NOTES) { type = NavType.LongType }
            )
        ) {
                navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getLong(KEY_ID_NOTES)
            DetailScreen(navController, id)
        }
    }
}