package com.wibisa.dicodingstoryapp.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wibisa.dicodingstoryapp.core.data.remote.network.StoryApi
import com.wibisa.dicodingstoryapp.core.data.remote.response.LoginResult
import com.wibisa.dicodingstoryapp.core.model.InputLogin
import com.wibisa.dicodingstoryapp.core.model.InputRegister
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: StoryApi,
    private val dataStore: DataStore<Preferences>
) {

    suspend fun register(inputRegister: InputRegister): ApiResult<String> {
        return try {
            val response = api.register(
                name = inputRegister.name,
                email = inputRegister.email,
                password = inputRegister.password
            )

            if (!response.error) {
                ApiResult.Success(response.message)
            } else {
                ApiResult.Error(response.message)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun login(inputLogin: InputLogin): ApiResult<LoginResult> {
        return try {
            val response = api.login(
                email = inputLogin.email,
                password = inputLogin.password
            )

            if (!response.error) {
                ApiResult.Success(response.loginResult)
            } else {
                ApiResult.Error(response.message)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun logout(): ApiResult<String> {
        return try {
            clearUserPreferencesFromDataStore()
            ApiResult.Success("Logout success")
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun saveUserPreferencesToDataStore(userPreferences: UserPreferences) {
        dataStore.edit {
            it[USER_ID] = userPreferences.userId
            it[TOKEN] = userPreferences.token
            it[NAME] = userPreferences.name
        }
    }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val userId = preferences[USER_ID] ?: EMPTY_STRING
        val token = preferences[TOKEN] ?: EMPTY_STRING
        val name = preferences[NAME] ?: EMPTY_STRING

        return UserPreferences(userId, token, name)
    }

    suspend fun fetchUserPreferences() = mapUserPreferences(dataStore.data.first().toPreferences())

    private suspend fun clearUserPreferencesFromDataStore() {
        dataStore.edit { it.clear() }
    }

    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val TOKEN = stringPreferencesKey("token")
        private val NAME = stringPreferencesKey("name")
        private const val EMPTY_STRING = ""
    }
}