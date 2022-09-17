package com.app.body_manage.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferenceRepository(
    private val context: Context,
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingPrefs")
        val KEY_TALL = floatPreferencesKey("key_tall")
        val KEY_WEIGHT = floatPreferencesKey("key_weight")
        val KEY_FAT = floatPreferencesKey("key_fat")
        val KEY_ALARM = booleanPreferencesKey("key_alarm")
    }

    suspend fun putTall(tall: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TALL] = tall
        }
    }

    suspend fun putWeight(weight: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_WEIGHT] = weight
        }
    }

    suspend fun putFat(fat: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FAT] = fat
        }
    }

    suspend fun putAlarm(onAlarm: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ALARM] = onAlarm
        }
    }

    val userPref: Flow<UserPreference> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            UserPreference(
                tall = it[KEY_TALL],
                weight = it[KEY_WEIGHT],
                fat = it[KEY_FAT],
                alarm = it[KEY_ALARM],
            )
        }
}