package com.wibisa.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.InputRegister
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.util.DataDummy
import com.wibisa.dicodingstoryapp.util.MainDispatcherRule
import com.wibisa.dicodingstoryapp.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyInputRegister = InputRegister("Jack", "jack@mail.com", "123456")

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(userRepository)
    }

    @Test
    fun `when register success, should return success response`() = runTest {
        val expected = MutableLiveData<ApiResult<String>>()
        expected.value = ApiResult.Success(DataDummy.dummyRegisterSuccessResponse)
        `when`(userRepository.register(dummyInputRegister)).thenReturn(expected)
        val actual = registerViewModel.register(dummyInputRegister).getOrAwaitValue()
        verify(userRepository).register(dummyInputRegister)
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `when register failed, should return failed response`() = runTest {
        val expected = MutableLiveData<ApiResult<String>>()
        expected.value = ApiResult.Error(DataDummy.dummyRegisterFailedResponse)
        `when`(userRepository.register(dummyInputRegister)).thenReturn(expected)
        val actual = registerViewModel.register(dummyInputRegister).getOrAwaitValue()
        verify(userRepository).register(dummyInputRegister)
        assertThat(actual).isEqualTo(expected.value)
    }

}