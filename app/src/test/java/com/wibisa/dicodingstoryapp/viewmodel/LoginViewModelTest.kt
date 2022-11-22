package com.wibisa.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.wibisa.dicodingstoryapp.core.data.remote.response.LoginResult
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.InputLogin
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var loginViewModel: LoginViewModel

    private val dummyInputLogin = InputLogin("jack@mail.com", "123456")
    private val dummyUserPreferences = UserPreferences("123", "asdfsdsw1", "Jack")

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(userRepository)
    }

    @Test
    fun `when login success, should return success response`() =
        runTest {
            val expected = MutableLiveData<ApiResult<LoginResult>>()
            expected.value = ApiResult.Success(DataDummy.dummyLoginResult)
            `when`(loginViewModel.login(dummyInputLogin)).thenReturn(expected)
            val actual = loginViewModel.login(dummyInputLogin).getOrAwaitValue()
            assertThat(actual).isEqualTo(expected.value)
            verify(userRepository).login(dummyInputLogin)
        }


    @Test
    fun `when login failed, should return failed response`() = runTest {
        val expected = MutableLiveData<ApiResult<LoginResult>>()
        expected.value = ApiResult.Error(DataDummy.dummyLoginFailedResponse)
        `when`(loginViewModel.login(dummyInputLogin)).thenReturn(expected)
        val actual = loginViewModel.login(dummyInputLogin).getOrAwaitValue()
        assertThat(actual).isEqualTo(expected.value)
        verify(userRepository).login(dummyInputLogin)
    }

    @Test
    fun `save token to data store successfully`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = dummyUserPreferences
        `when`(userRepository.fetchUserPreferences()).thenReturn(expected)
        loginViewModel.saveUserPreferences(dummyUserPreferences)
        val actual = userRepository.fetchUserPreferences().getOrAwaitValue()
        verify(userRepository).saveUserPreferencesToDataStore(dummyUserPreferences)
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isEqualTo(expected.value)
    }
}