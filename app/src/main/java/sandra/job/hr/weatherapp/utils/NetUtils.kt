package sandra.job.hr.weatherapp.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager

fun isConnectedToInternet(context: Context): Boolean {
    val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = manager.activeNetworkInfo
    return info != null && info.isConnected
}

