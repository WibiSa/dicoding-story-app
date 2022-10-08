package com.wibisa.dicodingstoryapp.ui.login

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
import com.wibisa.dicodingstoryapp.core.model.InputLogin
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.ApiResult
import com.wibisa.dicodingstoryapp.core.util.showToast
import com.wibisa.dicodingstoryapp.databinding.FragmentLoginBinding
import com.wibisa.dicodingstoryapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Membuat Custom View berupa EditText pada halaman login dan register

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val authNavController: NavController? by lazy { view?.findNavController() }
    private val baseNavController: NavController? by lazy { activity?.findNavController(R.id.base_nav_host) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLoginUiState()

        binding.btnLogin.setOnClickListener {
            // TODO: input validation here!
            login()
        }

        binding.tvGoToRegister.setOnClickListener {
            authNavController?.navigate(R.id.action_login_to_register)
        }
    }

    private fun login() {
        val email = binding.tfEmail.text.toString()
        val password = binding.tfPassword.text.toString()

        val inputLogin = InputLogin(email, password)

        viewModel.login(inputLogin)
    }

    private fun observeLoginUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginUiState.collect { loginUi ->
                    when (loginUi) {
                        is ApiResult.Success -> {
                            binding.loadingIndicator.hide()
                            val userPreferences = UserPreferences(
                                userId = loginUi.data.userId,
                                name = loginUi.data.name,
                                token = loginUi.data.token
                            )
                            viewModel.saveUserPreferences(userPreferences)
                            delay(800)
                            baseNavController?.navigate(R.id.action_baseAuthentication_to_baseMainFlow)
                            viewModel.loginCompleted()
                        }
                        is ApiResult.Loading -> {
                            binding.loadingIndicator.show()
                        }
                        is ApiResult.Error -> {
                            binding.loadingIndicator.hide()
                            requireContext().showToast(loginUi.message)
                            viewModel.loginCompleted()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}