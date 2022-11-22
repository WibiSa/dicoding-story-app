package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wibisa.dicodingstoryapp.core.data.repository.StoryRepository
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    val photoFile = MutableLiveData<File?>(null)

    suspend fun getUserPreferences() = userRepository.fetchUserPreferences()

    suspend fun addStory(userPreferences: UserPreferences, storyDesc: String, photoFile: File) =
        storyRepository.addStory(userPreferences, storyDesc, photoFile)
}