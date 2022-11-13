package com.wibisa.dicodingstoryapp.ui.storydetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.wibisa.dicodingstoryapp.core.util.loadImage
import com.wibisa.dicodingstoryapp.databinding.FragmentStoryDetailsBinding

class StoryDetailsFragment : Fragment() {

    private lateinit var binding: FragmentStoryDetailsBinding
    private val mainNavController: NavController? by lazy { view?.findNavController() }
    private val args: StoryDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appbar.setNavigationOnClickListener {
            mainNavController?.popBackStack()
        }

        bindStoryToView()
    }

    private fun bindStoryToView() {

        binding.apply {
            imgPhoto.loadImage(args.story.photoUrl)
            tvName.text = args.story.name
            tvDescription.text = args.story.description
        }
    }
}