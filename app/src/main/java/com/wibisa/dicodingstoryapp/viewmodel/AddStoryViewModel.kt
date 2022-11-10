package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository
) : ViewModel() {

    private val _userPreferences = MutableLiveData<UserPreferences>()
    val userPreferences: LiveData<UserPreferences>
        get() = _userPreferences

//    private val _photoTaken = MutableLiveData<String?>(null)
//    val photoTaken: LiveData<String?> = _photoTaken

    private val _photoFile = MutableLiveData<File?>(null)
    val photoFile: LiveData<File?> = _photoFile

    private val _addStoryUiState = MutableStateFlow<ApiResult<String>>(ApiResult.Empty)
    val addStoryUiState: StateFlow<ApiResult<String>> = _addStoryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _userPreferences.value = userRepository.fetchUserPreferences()
        }
    }

//    fun saveTemporarilyPhotoTake(photo: String) {
//        _photoTaken.value = photo
//    }

    fun saveTemporarilyPhotoFile(photoFile: File) {
        _photoFile.value = photoFile
    }

    fun clearPhotoInCache() {
//        _photoTaken.value?.let { File(it).delete() }
//        _photoTaken.value = null
        _photoFile.value?.delete()
        _photoFile.value = null
    }

    fun addStory(userPreferences: UserPreferences, storyDesc: String, photoFile: File) {
        viewModelScope.launch(Dispatchers.IO) {
            _addStoryUiState.value = ApiResult.Loading
            val response = storyRepository.addStory(userPreferences, storyDesc, photoFile)
            _addStoryUiState.value = response
        }
    }

    fun addStoryCompleted() {
        _addStoryUiState.value = ApiResult.Empty
    }
}