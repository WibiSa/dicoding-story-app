package com.wibisa.dicodingstoryapp.ui.addstory

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.core.model.UserPreferences
import com.wibisa.dicodingstoryapp.core.util.*
import com.wibisa.dicodingstoryapp.databinding.FragmentAddStoryBinding
import com.wibisa.dicodingstoryapp.ui.customview.LoadingDialog
import com.wibisa.dicodingstoryapp.viewmodel.AddStoryViewModel
import kotlinx.coroutines.launch

class AddStoryFragment : Fragment() {

    private lateinit var binding: FragmentAddStoryBinding
    private lateinit var loadingDialog: LoadingDialog
    private val viewModel: AddStoryViewModel by hiltNavGraphViewModels(R.id.add_story_flow)
    private val mainFlowNavController: NavController? by lazy { view?.findNavController() }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImage: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImage, requireContext())

                viewModel.saveTemporarilyPhotoFile(myFile)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.clearPhotoInCache()
                mainFlowNavController?.popBackStack()
            }
        })
    }

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

        loadingDialog = LoadingDialog(requireActivity())

        componentUiSetup()

        observePhoto(view)

        observeAddStoryUiState()
    }

    private fun componentUiSetup() {

        binding.appbar.setNavigationOnClickListener {
            viewModel.clearPhotoInCache()
            mainFlowNavController?.popBackStack()
        }

        binding.btnCamera.setOnClickListener {
            mainFlowNavController?.navigate(R.id.action_addStory_to_cameraPermission)
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnUpload.setOnClickListener {
            val isValid =
                binding.tfDescription.isNotNullOrEmpty(getString(R.string.validation_empty))

            if (isValid) {
                if (viewModel.photoFile.value != null)
                    observeUserPreferencesForAddStory()
                else
                    binding.addStoryContainer.showShortSnackBar(getString(R.string.validation_image_required))
            }
        }
    }

    private fun observePhoto(view: View) {
        viewModel.photoFile.observe(viewLifecycleOwner) { photo ->
            if (photo != null) {
                Glide.with(view).load(photo).into(binding.imgPreview)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun observeUserPreferencesForAddStory() {
        viewModel.userPreferences.observe(viewLifecycleOwner) {
            addStory(it)
        }
    }

    private fun addStory(userPreferences: UserPreferences) {
        val storyDesc = binding.tfDescription.text.toString()
        val photoFile = viewModel.photoFile.value!!

        val reducePhotoFile = reduceFileImage(photoFile)

        viewModel.addStory(userPreferences, storyDesc, reducePhotoFile)
    }

    private fun observeAddStoryUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addStoryUiState.collect { ui ->
                    when (ui) {
                        is ApiResult.Success -> {
                            loadingDialog.dismissLoadingDialog()

                            viewModel.clearPhotoInCache()

                            mainFlowNavController?.popBackStack()

                            requireContext().showToast(ui.data)

                            viewModel.addStoryCompleted()
                        }
                        is ApiResult.Loading -> {
                            loadingDialog.startLoadingDialog()
                        }
                        is ApiResult.Error -> {
                            loadingDialog.dismissLoadingDialog()

                            binding.addStoryContainer.showShortSnackBar(ui.message)

                            viewModel.addStoryCompleted()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}