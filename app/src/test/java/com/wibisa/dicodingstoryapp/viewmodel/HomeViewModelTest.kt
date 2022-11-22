package com.wibisa.dicodingstoryapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.google.common.truth.Truth.assertThat
import com.wibisa.dicodingstoryapp.adapter.StoriesAdapter
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.data.repository.StoryRepository
import com.wibisa.dicodingstoryapp.core.data.repository.UserRepository
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.util.DataDummy
import com.wibisa.dicodingstoryapp.util.MainDispatcherRule
import com.wibisa.dicodingstoryapp.util.PagedTestDataSource
import com.wibisa.dicodingstoryapp.util.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
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
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var homeViewModel: HomeViewModel

    private val dummyUserPreferences = UserPreferences("123", "asdfsdsw1", "Jack")

    @Before
    fun setUp() {
        homeViewModel = HomeViewModel(userRepository, storyRepository)
    }

    @Test
    fun `get user preferences from data store successfully`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = dummyUserPreferences
        `when`(homeViewModel.getUserPreferences()).thenReturn(expected)
        val actual = homeViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `get user preferences from data store empty`() = runTest {
        val expected = MutableLiveData<UserPreferences>()
        expected.value = null
        `when`(homeViewModel.getUserPreferences()).thenReturn(expected)
        val actual = homeViewModel.getUserPreferences().getOrAwaitValue()
        verify(userRepository).fetchUserPreferences()
        assertThat(actual).isNull()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `get stories success, if stories not null and stories size matching is true`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStoriesWithPaging(dummyUserPreferences)).thenReturn(stories)

        val actualStories = homeViewModel.getStories(dummyUserPreferences).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.COMPARATOR,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        verify(storyRepository).getAllStoriesWithPaging(dummyUserPreferences)
        assertThat(differ.snapshot()).isNotNull()
        assertThat(differ.snapshot().size).isEqualTo(dummyStories.size)
    }

    @Test
    fun `when logout success, should return success response`() = runTest {
        val expected = MutableLiveData<ApiResult<String>>()
        expected.value = ApiResult.Success("User logout.")
        `when`(userRepository.logout()).thenReturn(expected)
        val actual = homeViewModel.logout().getOrAwaitValue()
        verify(userRepository).logout()
        assertThat(actual).isEqualTo(expected.value)
    }

    @Test
    fun `when logout failed, should return failed response`() = runTest {
        val expected = MutableLiveData<ApiResult<String>>()
        expected.value = ApiResult.Error("User logout failed.")
        `when`(userRepository.logout()).thenReturn(expected)
        val actual = homeViewModel.logout().getOrAwaitValue()
        verify(userRepository).logout()
        assertThat(actual).isEqualTo(expected.value)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}