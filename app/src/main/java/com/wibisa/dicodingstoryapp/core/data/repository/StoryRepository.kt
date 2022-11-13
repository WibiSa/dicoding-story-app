package com.wibisa.dicodingstoryapp.core.data.repository

import com.wibisa.dicodingstoryapp.core.data.remote.network.StoryApi
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.core.util.StringTransform
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val api: StoryApi
) {

    suspend fun getAllStories(
        userPreferences: UserPreferences,
        needLocation: Int
    ): ApiResult<List<Story>> {
        return try {
            val token = StringTransform.tokenFormat(userPreferences.token)
            val response = api.getAllStories(token = token, location = needLocation)

            if (!response.error) {
                ApiResult.Success(response.listStory)
            } else {
                ApiResult.Error(response.message)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    suspend fun addStory(
        userPreferences: UserPreferences,
        storyDesc: String,
        photoFile: File
    ): ApiResult<String> {
        return try {
            val token = StringTransform.tokenFormat(userPreferences.token)
            val photo = MultipartBody.Part.createFormData(
                "photo", photoFile.name, photoFile.asRequestBody()
            )
            val description = MultipartBody.Part.createFormData(
                "description", storyDesc
            )

            val response = api.addStory(token = token, photo = photo, description = description)
            if (!response.error) {
                ApiResult.Success(response.message)
            } else {
                ApiResult.Error(response.message)
            }
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }
}