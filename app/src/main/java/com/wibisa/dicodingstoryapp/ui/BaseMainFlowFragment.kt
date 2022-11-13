package com.wibisa.dicodingstoryapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.core.util.hide
import com.wibisa.dicodingstoryapp.core.util.show
import com.wibisa.dicodingstoryapp.databinding.FragmentBaseMainFlowBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseMainFlowFragment : Fragment() {

    private lateinit var binding: FragmentBaseMainFlowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBaseMainFlowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainFlowNavController = requireActivity().findNavController(R.id.main_flow_nav_host)
        binding.bottomNav.setupWithNavController(mainFlowNavController)

        mainFlowNavController.addOnDestinationChangedListener { _, destination, _ ->
            val isValid = destination.id == R.id.homeScreen || destination.id == R.id.explore
            if (isValid)
                binding.bottomNav.show()
            else
                binding.bottomNav.hide()
        }
    }
}