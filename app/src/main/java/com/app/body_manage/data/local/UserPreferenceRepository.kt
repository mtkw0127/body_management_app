package com.app.body_manage.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate

class UserPreferenceRepository(
    private val context: Context,
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingPrefs")
        val KEY_NAME = stringPreferencesKey("key_name")
        val KEY_GENDER = intPreferencesKey("key_gender")
        val KEY_BIRTH = stringPreferencesKey("key_birth")
        val KEY_TALL = floatPreferencesKey("key_tall")
        val KEY_WEIGHT = floatPreferencesKey("key_weight")
        val KEY_GOAL_WEIGHT = floatPreferencesKey("key_goal_weight")
        val KEY_GOAL_KCAL = intPreferencesKey("key_goal_kcal")
        val KEY_FAT = floatPreferencesKey("key_fat")
        val KEY_ALARM = booleanPreferencesKey("key_alarm")
    }

    suspend fun setName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NAME] = name
        }
    }

    suspend fun setGender(gender: Gender) {
        context.dataStore.edit { preferences ->
            preferences[KEY_GENDER] = gender.value
        }
    }

    suspend fun setBirth(birth: LocalDate) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BIRTH] = "${birth.year}-${birth.monthValue}-${birth.dayOfMonth}"
        }
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

    suspend fun setGoatWeight(goalWeight: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_GOAL_WEIGHT] = goalWeight
        }
    }

    suspend fun setGoatKcal(goalKcal: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_GOAL_KCAL] = goalKcal
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
            val name = checkNotNull(it[KEY_NAME])
            val gender = when (checkNotNull(it[KEY_GENDER])) {
                Gender.MALE.value -> Gender.MALE
                Gender.FEMALE.value -> Gender.FEMALE
                else -> error("IllegalState ${it[KEY_GENDER]}")
            }
            val birth = checkNotNull(it[KEY_BIRTH]).let {
                val split = it.split("-")
                LocalDate.of(
                    split[0].toInt(),
                    split[1].toInt(),
                    split[2].toInt(),
                )
            }
            UserPreference(
                name = name,
                gender = gender,
                birth = birth,
                tall = it[KEY_TALL],
                weight = it[KEY_WEIGHT],
                goalWeight = it[KEY_GOAL_WEIGHT],
                goalKcal = it[KEY_GOAL_KCAL],
                fat = it[KEY_FAT],
                alarm = it[KEY_ALARM] ?: false,
            )
        }
}
