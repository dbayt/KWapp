package com.kwapp.utils

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.kwapp.retrofit.pojo.SearchHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "search_history")

class SearchHistoryManager(private val context: Context) {

    companion object {
        private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
        private const val MAX_HISTORY_SIZE = 5 // Limit to 5 items
    }

    // Save History to DataStore
    suspend fun saveSearchHistory(history: List<SearchHistoryItem>) {
        context.dataStore.edit { preferences ->
            val jsonString = Json.encodeToString(history)
            preferences[SEARCH_HISTORY_KEY] = jsonString
        }
    }

    // Get History as a Flow
    val searchHistory: Flow<List<SearchHistoryItem>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[SEARCH_HISTORY_KEY] ?: "[]"
            Json.decodeFromString(jsonString)
        }
}