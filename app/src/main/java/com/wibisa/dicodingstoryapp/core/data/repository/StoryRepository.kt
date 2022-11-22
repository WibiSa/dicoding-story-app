package com.wibisa.dicodingstoryapp.core.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.wibisa.dicodingstoryapp.core.data.StoryPagingSource
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

    fun getAllStoriesWithPaging(userPreferences: UserPreferences): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(
                    api = api,
                    userPreferences = userPreferences
                )
            }
        ).liveData
    }

    suspend fun getAllStoriesWithLocation(
        userPreferences: UserPreferences
    ): LiveData<ApiResult<List<Story>>> = liveData {
        emit(ApiResult.Loading)
        try {
            val token = StringTransform.tokenFormat(userPreferences.token)
            val response = api.getAllStoriesWithLocation(token = token)

            if (!response.error) {
                emit(ApiResult.Success(response.listStory))
            } else {
                emit(ApiResult.Error(response.message))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message.toString()))
        }
    }

    suspend fun addStory(
        userPreferences: UserPreferences,
        storyDesc: String,
        photoFile: File
    ): LiveData<ApiResult<String>> = liveData {
        emit(ApiResult.Loading)
        try {
            val token = StringTransform.tokenFormat(userPreferences.token)
            val photo = MultipartBody.Part.createFormData(
                "photo", photoFile.name, photoFile.asRequestBody()
            )
            val description = MultipartBody.Part.createFormData(
                "description", storyDesc
            )

            val response = api.addStory(token = token, photo = photo, description = description)
            if (!response.error) {
                emit(ApiResult.Success(response.message))
            } else {
                emit(ApiResult.Error(response.message))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message.toString()))
        }
    }
}