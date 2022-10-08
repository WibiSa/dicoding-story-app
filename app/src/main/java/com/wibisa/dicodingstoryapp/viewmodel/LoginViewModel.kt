package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wibisa.dicodingstoryapp.core.data.remote.response.LoginResult
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.InputLogin
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
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _loginUiState = MutableStateFlow<ApiResult<LoginResult>>(ApiResult.Empty)
    val loginUiState: StateFlow<ApiResult<LoginResult>> = _loginUiState.asStateFlow()

    fun login(inputLogin: InputLogin) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginUiState.value = ApiResult.Loading
            val response = userRepository.login(inputLogin)
            _loginUiState.value = response
        }
    }

    fun loginCompleted() {
        _loginUiState.value = ApiResult.Empty
    }

    fun saveUserPreferences(userPreferences: UserPreferences) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.saveUserPreferencesToDataStore(userPreferences)
        }
    }
}