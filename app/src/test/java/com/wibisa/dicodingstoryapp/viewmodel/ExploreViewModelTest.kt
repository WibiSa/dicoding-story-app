package com.wibisa.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.data.repository.StoryRepository
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ExploreViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var exploreViewModel: ExploreViewModel

    private val dummyUserPreferences = UserPreferences("123", "asdfsdsw1", "Jack")
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()

    @Before
    fun setUp() {
        exploreViewModel = ExploreViewModel(userRepository, storyRepository)
    }

    @Test
    fun `get user preferences from data store successfully`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = dummyUserPreferences
        `when`(exploreViewModel.getUserPreferences()).thenReturn(expected)
        val actual = exploreViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `get user preferences from data store empty`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = null
        `when`(exploreViewModel.getUserPreferences()).thenReturn(expected)
        val actual = exploreViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNull()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `get story with location success, should return success response`() = runTest {
        val expected = MutableLiveData<ApiResult<List<Story>>>()
        expected.value = ApiResult.Success(dummyStoriesResponse.listStory)
        `when`(exploreViewModel.getStories(dummyUserPreferences)).thenReturn(expected)
        val actual = exploreViewModel.getStories(dummyUserPreferences).getOrAwaitValue()
        verify(storyRepository).getAllStoriesWithLocation(dummyUserPreferences)
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `get story with location failed, should return failed response`() = runTest {
        val expected = MutableLiveData<ApiResult<List<Story>>>()
        expected.value = ApiResult.Error("Cannot fetch data.")
        `when`(exploreViewModel.getStories(dummyUserPreferences)).thenReturn(expected)
        val actual = exploreViewModel.getStories(dummyUserPreferences).getOrAwaitValue()
        verify(storyRepository).getAllStoriesWithLocation(dummyUserPreferences)
        assertThat(actual).isEqualTo(expected.value)
    }
}