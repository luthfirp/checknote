package org.d3if3082.checknote.navigation

import org.d3if3082.checknote.ui.screen.KEY_ID_NOTES

sealed class Screen(val route: String) {
    data object Home: Screen("mainScreen")
    data object Info: Screen("infoScreen")
    data object FormBaru: Screen("detailScreen")
    data object FormUbah: Screen("detailScreen/{$KEY_ID_NOTES}") {
        fun withId(id: Long) = "detailScreen/$id"
    }
}