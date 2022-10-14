package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.data.repository.StoryRepository
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _userPreferences = MutableLiveData<UserPreferences>()
    val userPreferences: LiveData<UserPreferences>
        get() = _userPreferences

    private val _storiesUiState = MutableStateFlow<ApiResult<List<Story>>>(ApiResult.Empty)
    val storiesUiState: StateFlow<ApiResult<List<Story>>> = _storiesUiState.asStateFlow()

    private val _logoutUiState = MutableStateFlow<ApiResult<String>>(ApiResult.Empty)
    val logoutUiState: StateFlow<ApiResult<String>> = _logoutUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _userPreferences.value = userRepository.fetchUserPreferences()
        }
    }

    fun getAllStories(userPreferences: UserPreferences) {
        viewModelScope.launch(Dispatchers.IO) {
            _storiesUiState.value = ApiResult.Loading
            val response = storyRepository.getAllStories(userPreferences)
            _storiesUiState.value = response
        }
    }

    fun getAllStoriesCompleted() {
        _storiesUiState.value = ApiResult.Empty
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            _logoutUiState.value = ApiResult.Loading
            val response = userRepository.logout()
            _logoutUiState.value = response
        }
    }

    fun logoutCompleted() {
        _logoutUiState.value = ApiResult.Empty
    }
}