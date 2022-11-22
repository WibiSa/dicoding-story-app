package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _splashIsLoading = MutableStateFlow(true)
    val splashIsLoading = _splashIsLoading.asStateFlow()

    suspend fun getUserPreferences() = userRepository.fetchUserPreferences().also {
        _splashIsLoading.value = false
    }
}