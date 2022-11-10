package com.wibisa.dicodingstoryapp.ui.camera

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.wibisa.dicodingstoryapp.R
import com.wibisa.dicodingstoryapp.databinding.FragmentCameraBinding
import com.wibisa.dicodingstoryapp.ui.permission.CameraPermissionFragment
import com.wibisa.dicodingstoryapp.viewmodel.AddStoryViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private val viewModel: AddStoryViewModel by hiltNavGraphViewModels(R.id.add_story_flow)
    private val mainFlowNavController: NavController? by lazy { view?.findNavController() }
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainFlowNavController?.popBackStack(R.id.addStory, false)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        componentUiSetup()
    }

    override fun onResume() {
        super.onResume()
        val isValid = !CameraPermissionFragment.hasCameraPermissions(requireContext())
        if (isValid)
            mainFlowNavController?.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun componentUiSetup() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        outputDirectory = getOutputDirectory()

        binding.cameraPreview.post {
            cameraSetup()

            binding.btnCameraShutter.setOnClickListener { takePhoto() }
        }

        binding.btnClose.setOnClickListener {
            mainFlowNavController?.popBackStack(R.id.addStory, false)
        }
    }

    private fun cameraSetup() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(640, 640))
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            try {
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e(TAG, "use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(outputDirectory)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(p0: ImageCapture.OutputFileResults) {
                    val savedUri = p0.savedUri ?: Uri.fromFile(photoFile)
                    Log.d(TAG, "photo capture succeeded: ${savedUri.path}")
                    savedUri.path?.let {
//                        viewModel.saveTemporarilyPhotoTake(it)
                        viewModel.saveTemporarilyPhotoFile(photoFile)
                        mainFlowNavController?.popBackStack(R.id.addStory, false)
                    }
                }

                override fun onError(p0: ImageCaptureException) {
                    Log.e(TAG, "photo capture failed: ${p0.message}", p0)
                }
            }
        )
    }

    private fun getOutputDirectory(): File {
        //save photo file in cache app.
        val mediaDir = requireContext().externalCacheDirs.firstOrNull()?.let {
            File(
                it,
                resources.getString(R.string.app_name)
            ).apply { mkdir() }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    companion object {
        private const val TAG = "Camera"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"

        private fun createFile(baseFile: File) =
            File(
                baseFile,
                SimpleDateFormat(
                    FILENAME_FORMAT,
                    Locale.getDefault()
                ).format(System.currentTimeMillis()) + PHOTO_EXTENSION
            )
    }
}