package com.wibisa.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.util.MainDispatcherRule
import com.wibisa.dicodingstoryapp.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var mainViewModel: MainViewModel

    private val dummyUserPreferences = UserPreferences("123", "asdfsdsw1", "Jack")

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(userRepository)
    }

    @Test
    fun `get user preferences from data store successfully`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = dummyUserPreferences
        `when`(mainViewModel.getUserPreferences()).thenReturn(expected)
        val actual = mainViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `get user preferences from data store empty`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = null
        `when`(mainViewModel.getUserPreferences()).thenReturn(expected)
        val actual = mainViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNull()
        assertThat(actual).isEqualTo(expected.value)
    }
}