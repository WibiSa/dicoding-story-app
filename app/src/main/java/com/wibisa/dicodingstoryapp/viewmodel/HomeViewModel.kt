package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _logoutUiState = MutableStateFlow<ApiResult<String>>(ApiResult.Empty)
    val logoutUiState: StateFlow<ApiResult<String>> = _logoutUiState.asStateFlow()

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