package com.rohit.customdownloadmanager.utils

import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker

object HelperExtensions {

    fun CoroutineWorker.toastShort(message: CharSequence) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun logi( message: String) {
        Log.i("Download Worker - ", message)
    }
}