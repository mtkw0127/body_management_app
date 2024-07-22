package com.app.body_manage.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
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
        val KEY_GOAL_KCAL = longPreferencesKey("key_goal_kcal")
        val KEY_START_WEIGHT = floatPreferencesKey("key_start_weight")
        val kEY_REQUESTED_REVIEW = booleanPreferencesKey("key_requested_review")
        val KEY_FAT = floatPreferencesKey("key_fat")
        val KEY_ALARM = booleanPreferencesKey("key_alarm")
        val KEY_OPTION_MEAL = booleanPreferencesKey("key_option_meal")
        val KEY_OPTION_TRAINING = booleanPreferencesKey("key_option_training")
    }

    suspend fun setOptionMeal(option: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OPTION_MEAL] = option
        }
    }

    suspend fun setOptionTraining(option: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_OPTION_TRAINING] = option
        }
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

    suspend fun setGoatKcal(goalKcal: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_GOAL_KCAL] = goalKcal
        }
    }

    suspend fun setRequestedReview() {
        context.dataStore.edit { preferences ->
            preferences[kEY_REQUESTED_REVIEW] = true
        }
    }

    suspend fun getRequestedReview(): Boolean = context.dataStore.data.map {
        it[kEY_REQUESTED_REVIEW] ?: false
    }.firstOrNull() ?: false

    val tall: Flow<Float?> = context.dataStore.data.map {
        it[KEY_TALL]
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
                else -> null
            }
            val birth = checkNotNull(it[KEY_BIRTH]).let { birth ->
                val split = birth.split("-")
                LocalDate.of(
                    split[0].toInt(),
                    split[1].toInt(),
                    split[2].toInt(),
                )
            }
            UserPreference(
                name = name,
                gender = gender ?: Gender.MALE,
                birth = birth ?: LocalDate.now(),
                startWeight = it[KEY_START_WEIGHT],
                goalWeight = it[KEY_GOAL_WEIGHT],
                goalKcal = it[KEY_GOAL_KCAL],
                optionFeature = UserPreference.OptionFeature(
                    meal = it[KEY_OPTION_MEAL] ?: false,
                    training = it[KEY_OPTION_TRAINING] ?: false,
                ),
                alarm = it[KEY_ALARM] ?: false,
            )
        }
}
