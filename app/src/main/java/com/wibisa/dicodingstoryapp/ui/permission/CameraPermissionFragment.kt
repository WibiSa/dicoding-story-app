package com.wibisa.dicodingstoryapp.ui.permission

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.wibisa.dicodingstoryapp.R

class CameraPermissionFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkIfPermissionIsGranted()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        navigateToNextDestination()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun checkIfPermissionIsGranted() {
        if (hasCameraPermissions(requireContext())) {
            navigateToNextDestination()
        } else {
            requestPermissions()
        }
    }

    private fun navigateToNextDestination() {
        lifecycleScope.launchWhenCreated {
            findNavController().navigate(R.id.action_cameraPermission_to_camera)
        }
    }

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.accept_camera_permission),
            REQUEST_CODE_CAMERA_PERMISSION,
            Manifest.permission.CAMERA
        )
    }

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 1

        fun hasCameraPermissions(context: Context) =
            EasyPermissions.hasPermissions(context, Manifest.permission.CAMERA)
    }
}