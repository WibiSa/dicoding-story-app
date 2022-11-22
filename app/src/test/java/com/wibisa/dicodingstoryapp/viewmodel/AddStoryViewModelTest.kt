package com.wibisa.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.wibisa.dicodingstoryapp.core.data.repository.StoryRepository
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
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
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var addStoryViewModel: AddStoryViewModel

    private val dummyUserPreferences = UserPreferences("123", "asdfsdsw1", "Jack")
    private val dummyStoryDesc = "Lorem ipsum elsie."
    private val dummyPhotoFile = File("example")

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(userRepository, storyRepository)
    }

    @Test
    fun `get user preferences from data store successfully`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = dummyUserPreferences
        `when`(addStoryViewModel.getUserPreferences()).thenReturn(expected)
        val actual = addStoryViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `get user preferences from data store empty`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = null
        `when`(addStoryViewModel.getUserPreferences()).thenReturn(expected)
        val actual = addStoryViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNull()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `add story success, should return success response`() = runTest {
        val expected = MutableLiveData<ApiResult<String>>()
        expected.value = ApiResult.Success("Story create success.")

        `when`(
            storyRepository.addStory(
                dummyUserPreferences,
                dummyStoryDesc,
                dummyPhotoFile
            )
        ).thenReturn(expected)

        val actual =
            addStoryViewModel.addStory(dummyUserPreferences, dummyStoryDesc, dummyPhotoFile)
                .getOrAwaitValue()

        verify(storyRepository).addStory(dummyUserPreferences, dummyStoryDesc, dummyPhotoFile)

        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `add story failed, should return failed response`() = runTest {
        val expected = MutableLiveData<ApiResult<String>>()
        expected.value = ApiResult.Error("Story create failed.")

        `when`(
            storyRepository.addStory(
                dummyUserPreferences,
                dummyStoryDesc,
                dummyPhotoFile
            )
        ).thenReturn(expected)

        val actual =
            addStoryViewModel.addStory(dummyUserPreferences, dummyStoryDesc, dummyPhotoFile)
                .getOrAwaitValue()

        verify(storyRepository).addStory(dummyUserPreferences, dummyStoryDesc, dummyPhotoFile)

        assertThat(actual).isEqualTo(expected.value)
    }
}