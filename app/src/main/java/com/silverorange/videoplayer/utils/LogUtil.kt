package com.silverorange.videoplayer.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.silverorange.videoplayer.BuildConfig
// mostly used to print Log only in debug mode
object LogUtil {
    private fun isDebugMode(): Boolean {
        return BuildConfig.isDebugMode
    }

    fun e(tag: String, msg: String) {
        if (isDebugMode()) {
            Log.e(tag, msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (isDebugMode()) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (isDebugMode()) {
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (isDebugMode()) {
            Log.i(tag, msg)
        }
    }

    fun println(tag: String, msg: String) {
        if (isDebugMode()) {
            println("======== $tag ====> $msg")
        }
    }

    fun toastShort(context: Context?, msg: String) {
        if (isDebugMode()){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }


}