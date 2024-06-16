package org.d3if3082.checknote.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.d3if3082.checknote.model.User
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

val Context.userDataStore : DataStore<Preferences> by preferencesDataStore(
    name = "user_preference"
)
class UserDataStore(private val context: Context) {
    companion object {
        private val USER_NAME = stringPreferencesKey("name")
        private val USER_EMAIL = stringPreferencesKey("email")
        private val USER_PHOTO = stringPreferencesKey("photoUrl")
    }

    val userFlow: Flow<User> = context.settingsDataStore.data.map { preferences ->
        User(
            name = preferences[USER_NAME] ?: "",
            email = preferences[USER_EMAIL] ?: "",
            photoUrl = preferences[USER_PHOTO] ?: "",
        )
    }

    suspend fun saveData(user: User) {
        context.settingsDataStore.edit { preferences ->
            preferences[USER_NAME] = user.name
            preferences[USER_EMAIL] = user.email
            preferences[USER_PHOTO] = user.photoUrl
        }
    }
}