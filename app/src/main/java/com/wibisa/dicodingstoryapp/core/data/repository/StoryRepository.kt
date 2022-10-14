package com.wibisa.dicodingstoryapp.core.data.repository

import com.wibisa.dicodingstoryapp.core.data.remote.network.StoryApi
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.core.util.StringTransform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val api: StoryApi
) {

    suspend fun getAllStories(userPreferences: UserPreferences): ApiResult<List<Story>> {
        return try {
            val token = StringTransform.tokenFormat(userPreferences.token)
            val response = api.getAllStories(token = token)

            if (!response.error) {
                ApiResult.Success(response.listStory)
            } else {
                ApiResult.Error(response.message)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }
}