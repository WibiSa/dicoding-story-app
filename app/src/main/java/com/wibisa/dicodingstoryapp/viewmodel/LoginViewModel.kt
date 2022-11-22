package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.InputLogin
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    suspend fun login(inputLogin: InputLogin) = userRepository.login(inputLogin)

    fun saveUserPreferences(userPreferences: UserPreferences) {
        viewModelScope.launch {
            userRepository.saveUserPreferencesToDataStore(userPreferences)
        }
    }
}