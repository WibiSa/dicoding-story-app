package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.InputRegister
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    private val _registerUiState = MutableStateFlow<ApiResult<String>>(ApiResult.Empty)
    val registerUiState: StateFlow<ApiResult<String>> = _registerUiState.asStateFlow()

    fun register(inputRegister: InputRegister) {
        viewModelScope.launch(Dispatchers.IO) {
            _registerUiState.value = ApiResult.Loading
            val response = userRepository.register(inputRegister)
            _registerUiState.value = response
        }
    }

    fun registerCompleted() {
        _registerUiState.value = ApiResult.Empty
    }
}