package com.silverorange.videoplayer.utils

import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Window
import android.widget.TextView
import com.silverorange.videoplayer.R

object NetworkUtil {
    /**
     * used for network related task
     **/
    //used for checking internet connection is available or not
    fun isNetworkAvailable(context: Context?): Boolean {
        var status = false
        if (context == null) {
            return false
        }
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        status = true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        status = true
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        status = true
                    }
                }
            } else {
                try {
                    val activeNetworkInfo =
                        connectivityManager.activeNetworkInfo
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        status = true
                    }
                } catch (e: java.lang.Exception) {
                    LogUtil.i("update_statut", "" + e.message)
                }
            }
        }
        if (!status) {
            //if not internet than show dialog with msg no internet connection
            openNoInternetDialog(context)
        }
        return status
    }

    private fun openNoInternetDialog(mContext: Context) {
        //dialog for showing not internet connection msg
        val dialog = Dialog(mContext, R.style.DialogWithoutTitle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutsom_dialog)
        val tvMsg = dialog.findViewById(R.id.tvMsg) as TextView
        tvMsg.text = mContext.resources.getString(R.string.no_internet_connection)
        val tvOk = dialog.findViewById(R.id.tvOk) as TextView
        tvOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}