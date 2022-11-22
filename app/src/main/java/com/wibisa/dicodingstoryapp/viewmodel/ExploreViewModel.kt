package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.wibisa.dicodingstoryapp.core.data.repository.StoryRepository
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    suspend fun getUserPreferences() = userRepository.fetchUserPreferences()

    suspend fun getStories(userPreferences: UserPreferences) =
        storyRepository.getAllStoriesWithLocation(userPreferences)
}