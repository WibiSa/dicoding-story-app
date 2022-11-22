package com.wibisa.dicodingstoryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.InputRegister
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val userRepository: UserRepository) :
    ViewModel() {

    suspend fun register(inputRegister: InputRegister) = userRepository.register(inputRegister)
}