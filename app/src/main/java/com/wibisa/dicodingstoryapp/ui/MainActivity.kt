package com.wibisa.dicodingstoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val baseNavController: NavController? by lazy { findNavController(R.id.base_nav_host) }
    private val viewModel: MainViewModel by viewModels()
    private var isFirstTimeLaunchApp: Boolean = FIRST_TIME_LAUNCH_APP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.splashIsLoading.value }
        }

        setContentView(R.layout.activity_main)

        observeUserPreferencesForCheckLoginState()
    }

    private fun observeUserPreferencesForCheckLoginState() {
        viewModel.userPreferences.observe(this) {
            checkLoginState(it.token)
        }
    }

    private fun checkLoginState(token: String) {
        val isValid = token.isNotEmpty() and isFirstTimeLaunchApp
        if (isValid) {
            baseNavController?.navigate(R.id.action_baseAuthentication_to_baseMainFlow)
            isFirstTimeLaunchApp = !isFirstTimeLaunchApp
        }
    }

    companion object {
        private const val FIRST_TIME_LAUNCH_APP = true
    }
}