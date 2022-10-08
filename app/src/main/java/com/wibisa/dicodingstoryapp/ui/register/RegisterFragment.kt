package com.wibisa.dicodingstoryapp.ui.register

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
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.core.model.InputRegister
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.core.util.showToast
import com.wibisa.dicodingstoryapp.databinding.FragmentRegisterBinding
import com.wibisa.dicodingstoryapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// TODO: Membuat Custom View berupa EditText pada halaman login dan register

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private val authNavController: NavController? by lazy { view?.findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeRegisterUiState()

        binding.btnBack.setOnClickListener { }

        binding.btnRegister.setOnClickListener {
            // TODO: input validation here!
            register()
        }
    }

    private fun register() {
        val name = binding.tfName.text.toString()
        val email = binding.tfEmail.text.toString()
        val password = binding.tfPassword.text.toString()

        val inputRegister = InputRegister(name, email, password)

        viewModel.register(inputRegister)
    }

    private fun observeRegisterUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerUiState.collect { registerUi ->
                    when (registerUi) {
                        is ApiResult.Success -> {
                            binding.loadingIndicator.hide()
                            requireContext().showToast(registerUi.data)
                            authNavController?.popBackStack(R.id.login, false)
                            viewModel.registerCompleted()
                        }
                        is ApiResult.Loading -> {
                            binding.loadingIndicator.show()
                        }
                        is ApiResult.Error -> {
                            binding.loadingIndicator.hide()
                            requireContext().showToast(registerUi.message)
                            viewModel.registerCompleted()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}