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
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.core.util.showToast
import com.wibisa.dicodingstoryapp.databinding.FragmentHomeBinding
import com.wibisa.dicodingstoryapp.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
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

        observeLogoutUiState()

        binding.btnLogout.setOnClickListener {
            makeLogoutDialogAlert()
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
                            baseNavController?.navigate(R.id.action_baseMainFlow_to_baseAuthentication)
                            requireContext().showToast(logoutUi.data)
                            viewModel.logoutCompleted()
                        }
                        is ApiResult.Loading -> {}
                        is ApiResult.Error -> {
                            requireContext().showToast(logoutUi.message)
                            viewModel.logoutCompleted()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}