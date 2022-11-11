package com.wibisa.dicodingstoryapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.adapter.StoriesAdapter
import com.wibisa.dicodingstoryapp.adapter.StoryListener
import com.wibisa.dicodingstoryapp.core.data.remote.response.Story
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.*
import com.wibisa.dicodingstoryapp.databinding.FragmentHomeBinding
import com.wibisa.dicodingstoryapp.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: StoriesAdapter
    private val viewModel: HomeViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }
    private val baseNavController: NavController? by lazy { activity?.findNavController(R.id.base_nav_host) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserPreferencesForGetStories()

        observeStoriesUiState()

        observeLogoutUiState()

        componentUiSetup()
    }

    private fun componentUiSetup() {

        binding.appbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    makeLogoutDialogAlert()
                    true
                }
                else -> false
            }
        }

        binding.btnAddStory.setOnClickListener {
            mainNavController?.navigate(R.id.action_homeScreen_to_add_story_flow)
        }

        adapter = StoriesAdapter(StoryListener {
            val action = HomeFragmentDirections.actionHomeScreenToStoryDetails(it)
            mainNavController?.navigate(action)
        })
        binding.rvStories.adapter = adapter

        binding.btnReloadStory.setOnClickListener {
            observeUserPreferencesForGetStories()
        }
    }

    private fun observeUserPreferencesForGetStories() {
        viewModel.userPreferences.observe(viewLifecycleOwner) {
            getStories(it)
        }
    }

    private fun getStories(userPreferences: UserPreferences) {
        viewModel.getAllStories(userPreferences)
    }

    private fun observeStoriesUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.storiesUiState.collect { ui ->
                    when (ui) {
                        is ApiResult.Success -> {
                            binding.loadingIndicator.hide()
                            adapter.submitList(ui.data)
                            emptyStateStoryCheck(ui.data)
                            viewModel.getAllStoriesCompleted()
                        }
                        is ApiResult.Loading -> {
                            binding.loadingIndicator.show()
                            binding.homeEmptyStateContainer.hide()
                        }
                        is ApiResult.Error -> {
                            binding.loadingIndicator.hide()
                            binding.homeContainer.showShortSnackBar(ui.message)
                            binding.homeEmptyStateContainer.show()
                            viewModel.getAllStoriesCompleted()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun emptyStateStoryCheck(stories: List<Story>) {
        if (stories.isNotEmpty()) {
            binding.rvStories.show()
            binding.homeEmptyStateContainer.hide()
        } else {
            binding.rvStories.hide()
            binding.homeEmptyStateContainer.show()
        }
    }

    private fun makeLogoutDialogAlert() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Attention")
            .setMessage("Logout form App?")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .setPositiveButton("Yes") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun logout() {
        viewModel.logout()
    }

    private fun observeLogoutUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logoutUiState.collect { logoutUi ->
                    when (logoutUi) {
                        is ApiResult.Success -> {
                            binding.loadingIndicator.hide()
                            baseNavController?.navigate(R.id.action_baseMainFlow_to_baseAuthentication)
                            requireContext().showToast(logoutUi.data)
                            viewModel.logoutCompleted()
                        }
                        is ApiResult.Loading -> {
                            binding.loadingIndicator.show()
                        }
                        is ApiResult.Error -> {
                            binding.loadingIndicator.hide()
                            binding.homeContainer.showShortSnackBar(logoutUi.message)
                            viewModel.logoutCompleted()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}