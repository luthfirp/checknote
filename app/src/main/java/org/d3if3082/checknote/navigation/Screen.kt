package org.d3if3082.checknote.navigation

sealed class Screen(val route: String) {
    data object Home: Screen("mainScreen")
    data object Info: Screen("infoScreen")
    data object Add: Screen("addScreen")
}