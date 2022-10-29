package com.wibisa.dicodingstoryapp.ui.addstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wibisa.dicodingstoryapp.databinding.FragmentAddStoryBinding

class AddStoryFragment : Fragment() {

    private lateinit var binding: FragmentAddStoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        componentUiSetup()
    }

    private fun componentUiSetup() {


    }
}

// TODO: make permission fragment
// TODO: make camera fragment
// TODO: make function get foto from gallery