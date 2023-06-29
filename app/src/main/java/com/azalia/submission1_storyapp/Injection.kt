package com.azalia.submission1_storyapp

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.azalia.submission1_storyapp.data.Repository
import com.azalia.submission1_storyapp.data.local.StoryDatabase

object Injection {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    fun provideRepository(context: Context): Repository {
        val preference = UserPreference(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        return Repository(preference, database)
    }
}