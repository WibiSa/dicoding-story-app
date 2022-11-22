package com.wibisa.dicodingstoryapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.paging.LoadState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.adapter.StoriesAdapter
import com.wibisa.dicodingstoryapp.adapter.StoryListener
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

        componentUiSetup()
    }

    private fun componentUiSetup() {

        binding.appbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add_story -> {
                    mainNavController?.navigate(R.id.action_homeScreen_to_add_story_flow)
                    true
                }
                R.id.logout -> {
                    makeLogoutDialogAlert()
                    true
                }
                else -> false
            }
        }

        adapter = StoriesAdapter(StoryListener {
            val action = HomeFragmentDirections.actionHomeScreenToStoryDetails(it)
            mainNavController?.navigate(action)
        })
        binding.rvStories.adapter = adapter

        pagingLoadState()

        binding.btnReloadStory.setOnClickListener {
            observeUserPreferencesForGetStories()
        }
    }

    private fun observeUserPreferencesForGetStories() {
        lifecycleScope.launch {
            viewModel.getUserPreferences().observe(viewLifecycleOwner) { userPreferences ->
                viewModel.getStories(userPreferences).observe(viewLifecycleOwner) { stories ->
                    adapter.submitData(lifecycle, stories)
                }
            }
        }
    }

    private fun pagingLoadState() {
        adapter.addLoadStateListener { loadState ->

            when (loadState.refresh) {
                is LoadState.NotLoading -> {
                    binding.loadingIndicator.hide()
                    binding.rvStories.show()
                    binding.homeEmptyStateContainer.hide()
                }
                is LoadState.Loading -> {
                    binding.loadingIndicator.show()
                    binding.homeEmptyStateContainer.hide()
                }
                is LoadState.Error -> {
                    binding.loadingIndicator.hide()
                    val errorState = loadState.refresh as LoadState.Error
                    binding.homeContainer.showShortSnackBar(errorState.error.message.toString())
                    binding.rvStories.hide()
                    binding.homeEmptyStateContainer.show()
                }
            }
        }
    }

    private fun makeLogoutDialogAlert() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.attention))
            .setMessage(getString(R.string.sure_wanna_logout))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun logout() {
        lifecycleScope.launch {
            viewModel.logout().observe(viewLifecycleOwner) { logoutUi ->
                when (logoutUi) {
                    is ApiResult.Success -> {
                        binding.loadingIndicator.hide()
                        baseNavController?.navigate(R.id.action_baseMainFlow_to_baseAuthentication)
                        requireContext().showToast(logoutUi.data)
                    }
                    is ApiResult.Loading -> {
                        binding.loadingIndicator.show()
                    }
                    is ApiResult.Error -> {
                        binding.loadingIndicator.hide()
                        binding.homeContainer.showShortSnackBar(logoutUi.message)
                    }
                    else -> {}
                }
            }
        }
    }

}