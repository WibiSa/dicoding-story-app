package com.wibisa.dicodingstoryapp.core.util

import com.google.android.gms.maps.model.LatLng

const val BASE_URL ="https://story-api.dicoding.dev/v1/"

val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

val indonesiaLocation = LatLng(-2.3932797, 108.8507139)


//    fun registerX(inputRegister: InputRegister): StateFlow<ApiResult<String>>{
//        viewModelScope.launch(Dispatchers.IO) {
//            _registerUiState.value = ApiResult.Loading
//            val response = userRepository.register(inputRegister)
//            _registerUiState.value = response
//        }
//    }