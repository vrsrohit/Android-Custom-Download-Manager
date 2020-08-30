package com.rohit.customdownloadmanager.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

object HelperExtensions {

    fun Context.toastShort(message: CharSequence) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun logi( message: String) {
        Log.i("Download Worker - ", message)
    }
}