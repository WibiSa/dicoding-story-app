package com.wibisa.dicodingstoryapp.ui.customview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import com.wibisa.dicodingstoryapp.R

class LoadingDialog(private val activity: Activity) {

    private lateinit var dialog: AlertDialog

    @SuppressLint("InflateParams")
    fun startLoadingDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        dialogBuilder.setView(inflater.inflate(R.layout.loading_dialog, null))
        dialogBuilder.setCancelable(false)

        dialog = dialogBuilder.create()
        dialog.show()
    }

    fun dismissLoadingDialog() {
        dialog.dismiss()
    }
}