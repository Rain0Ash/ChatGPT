package com.rain0ash.chatgpt

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper

class VPNChecker(private val activity: MainActivity) {
    private var isVPN = active()
    val handler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            val state = active()

            if (isVPN != state) {
                isVPN = state
                activity.update()
            }

            handler.postDelayed(this, 1000)
        }
    }

    @Suppress("DEPRECATION")
    private fun active(): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.allNetworks.any { network ->
            val networkCapabilities = cm.getNetworkCapabilities(network)
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
        }
    }
}